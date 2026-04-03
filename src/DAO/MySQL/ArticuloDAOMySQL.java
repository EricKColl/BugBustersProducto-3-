package DAO.MySQL;

import DAO.Interfaces.ArticuloDAO;
import Excepciones.DAOException;
import Modelo.Articulo;

import java.sql.CallableStatement;
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
        // Llamada al procedimiento almacenado
        String sql = "{call insertar_articulo(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, articulo.getCodigo());
            cs.setString(2, articulo.getDescripcion());
            cs.setDouble(3, articulo.getPrecioVenta());
            cs.setDouble(4, articulo.getGastosEnvio());
            cs.setInt(5, articulo.getTiempoPreparacionMin());

            cs.execute();
            // MySQL ya hizo el COMMIT internamente
        } catch (SQLException e) {
            throw new DAOException("Error al insertar artículo mediante procedimiento: " + e.getMessage(), e);
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
        String sql = "SELECT COUNT(*) FROM pedidos p JOIN articulos a ON p.id_articulo = a.id_articulo WHERE a.codigo = ?";
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
        // 1. Primero comprobamos si tiene pedidos (lógica de negocio que ya tenías)
        if (tienePedidosAsociados(codigo)) {
            throw new DAOException("No se puede eliminar el artículo porque tiene pedidos asociados.");
        }

        // 2. Buscamos el ID interno que corresponde a ese código para el procedimiento
        int idArticulo = -1;
        String sqlBusqueda = "SELECT id_articulo FROM articulos WHERE codigo = ?";

        try (PreparedStatement psBusqueda = conexion.prepareStatement(sqlBusqueda)) {
            psBusqueda.setString(1, codigo);
            try (ResultSet rs = psBusqueda.executeQuery()) {
                if (rs.next()) {
                    idArticulo = rs.getInt("id_articulo");
                } else {
                    throw new DAOException("El artículo con código " + codigo + " no existe.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el ID del artículo: " + e.getMessage(), e);
        }

        // 3. Llamamos al procedimiento usando el ID encontrado
        String sqlProc = "{CALL eliminar_articulo(?)}";
        try (CallableStatement cs = conexion.prepareCall(sqlProc)) {
            cs.setInt(1, idArticulo);
            cs.execute();
            // El COMMIT ya lo hace el procedimiento en MySQL
        } catch (SQLException e) {
            throw new DAOException("Error al ejecutar eliminar_articulo: " + e.getMessage(), e);
        }
    }
}