package DAO.MySQL;

import DAO.Interfaces.ArticuloDAO;
import Excepciones.DAOException;
import Modelo.Articulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAOMySQL implements ArticuloDAO {

    private final Connection conexion;

    public ArticuloDAOMySQL(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Articulo articulo) throws DAOException {
        String sql = "INSERT INTO articulos (codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion) VALUES (?, ?, ?, ?, ?)";

        boolean autoCommitAnterior;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement stat = conexion.prepareStatement(sql)) {
                stat.setString(1, articulo.getCodigo());
                stat.setString(2, articulo.getDescripcion());
                stat.setDouble(3, articulo.getPrecioVenta());
                stat.setDouble(4, articulo.getGastosEnvio());
                stat.setInt(5, articulo.getTiempoPreparacionMin());

                int filasAfectadas = stat.executeUpdate();
                if (filasAfectadas == 0) {
                    conexion.rollback();
                    throw new DAOException("Error: No se ha insertado el artículo.", new SQLException());
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error de SQL al intentar guardar el artículo.", e);
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al gestionar la transacción al insertar artículo.", e);
        }
    }

    @Override
    public List<Articulo> obtenerTodos() throws DAOException {
        List<Articulo> listaArticulos = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, precio_venta, gastos_envio, tiempo_preparacion FROM articulos";

        try (PreparedStatement stat = conexion.prepareStatement(sql);
             ResultSet rs = stat.executeQuery()) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String descripcion = rs.getString("descripcion");
                double precioVenta = rs.getDouble("precio_venta");
                double gastosEnvio = rs.getDouble("gastos_envio");
                int tiempoPreparacionMin = rs.getInt("tiempo_preparacion");

                Articulo articulo = new Articulo(codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin);
                listaArticulos.add(articulo);
            }

        } catch (SQLException e) {
            throw new DAOException("Error de SQL al intentar obtener la lista de artículos.", e);
        }

        return listaArticulos;
    }

    @Override
    public Articulo obtenerPorId(String codigo) throws DAOException {
        Articulo articuloEncontrado = null;
        String sql = "SELECT * FROM articulos WHERE codigo = ?";

        try (PreparedStatement stat = conexion.prepareStatement(sql)) {
            stat.setString(1, codigo);

            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    articuloEncontrado = new Articulo(
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio_venta"),
                            rs.getDouble("gastos_envio"),
                            rs.getInt("tiempo_preparacion")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error de SQL al buscar el artículo con código: " + codigo, e);
        }

        return articuloEncontrado;
    }

    private boolean tienePedidosAsociados(String codigoArticulo) {
        String sql = """
                SELECT COUNT(*)
                FROM pedidos p
                JOIN articulos a ON p.id_articulo = a.id_articulo
                WHERE a.codigo = ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigoArticulo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            return true;
        }

        return false;
    }

    @Override
    public void eliminar(String codigo) throws DAOException {
        String sql = "DELETE FROM articulos WHERE codigo = ?";

        if (tienePedidosAsociados(codigo)) {
            throw new DAOException("No se puede eliminar el artículo porque tiene pedidos asociados.");
        }

        boolean autoCommitAnterior = true;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement stat = conexion.prepareStatement(sql)) {
                stat.setString(1, codigo);
                stat.executeUpdate();
                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al eliminar el artículo de la base de datos.", e);
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error crítico en la conexión al intentar eliminar.", e);
        }
    }
}