package DAO.MySQL;

import DAO.Interfaces.ArticuloDAO;
import Modelo.Articulo;
import Excepciones.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Usamos "implements" para obligarnos a cumplir el contrato de la interfaz
public class ArticuloDAOMySQL implements ArticuloDAO {

    // Conexión para hablar con MySQL
    private final Connection conexion;

    public ArticuloDAOMySQL(Connection conexion) {
        this.conexion = conexion;
    }

    // MÉTODO INSERTAR
    @Override
    public void insertar(Articulo articulo) throws DAOException {
        // Usamos ? en lugar de concatenar strings. Esto evita la "Inyección SQL" (hackeos).
        String sql = "INSERT INTO articulos (codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin) VALUES (?, ?, ?, ?, ?)";

        // Usamos try-catch para que cierre PreparedStatement automáticamente
        try (PreparedStatement stat = conexion.prepareStatement(sql)) {

            // Sustituimos las ? por los valores reales del artículo
            stat.setString(1, articulo.getCodigo());
            stat.setString(2, articulo.getDescripcion());
            stat.setDouble(3, articulo.getPrecioVenta());
            stat.setDouble(4, articulo.getGastosEnvio());
            stat.setInt(5, articulo.getTiempoPreparacionMin());

            // executeUpdate() Devuelve las filas afectadas en un INSERT, DELETE, UPDATE
            int filasAfectadas = stat.executeUpdate();
            if (filasAfectadas == 0) {
                SQLException e = new SQLException();
                throw new DAOException("Error: No se ha insertado el artículo.", e);
            }

        } catch (SQLException e) {
            // Atrapamos el error de MySQL y lanzamos el nuestro
            throw new DAOException("Error de SQL al intentar guardar el artículo.", e);
        }
    }

    // MÉTODO OBTENER TODOS
    @Override
    public List<Articulo> obtenerTodos() throws DAOException {
        List<Articulo> listaArticulos = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin FROM articulos";

        try (PreparedStatement stat = conexion.prepareStatement(sql);
             ResultSet rs = stat.executeQuery()) { // executeQuery() se usa SOLO para SELECT

            // rs.next() va leyendo fila por fila de la tabla en MySQL
            while (rs.next()) {
                // Extraemos los datos de la fila actual...
                String codigo = rs.getString("codigo");
                String descripcion = rs.getString("descripcion");
                double precioVenta = rs.getDouble("precioVenta");
                double gastosEnvio = rs.getDouble("gastosEnvio");
                int tiemposPreparacionMin = rs.getInt("tiemposPreparaciónMin");

                // Creamos un objeto Articulo de nuestro Modelo
                Articulo a = new Articulo(codigo, descripcion, precioVenta, gastosEnvio, tiemposPreparacionMin);

                listaArticulos.add(a);
            }

        } catch (SQLException e) {
            throw new DAOException("Error de SQL al intentar obtener la lista de artículos.", e);
        }

        return listaArticulos;
    }
    // MÉTODO OBTENER TODOS
    @Override
    public Articulo obtenerPorId(String codigo) throws DAOException {
        Articulo articuloEncontrado = null;
        String sql = "SELECT * FROM articulos WHERE codigo = ?";

        try (PreparedStatement stat = conexion.prepareStatement(sql)) {

            stat.setString(1, codigo);

            try (ResultSet rs = stat.executeQuery()) {

                if (rs.next()) {
                    // Si entra aquí, es que MySQL ha encontrado la fila
                    articuloEncontrado = new Articulo(
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getDouble("precioVenta"),
                            rs.getDouble("gastosEnvio"),
                            rs.getInt("tiemposPreparaciónMin")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error de SQL al buscar el artículo con código: " + codigo, e);
        }

        // Devolvemos el artículo (o null si el if fue falso y no se encontró nada)
        return articuloEncontrado;
    }


}