package DAO.MySQL;

import DAO.Interfaces.PedidoDAO;
import Modelo.Pedido;
import Util.ConexionBD;
import Excepciones.DAOException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOMySQL implements PedidoDAO {

    // --- MÉTODO AUXILIAR PARA MAPEAR ---
    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        int idPedido = rs.getInt("id_pedido");
        int idCliente = rs.getInt("id_cliente");
        int idArticulo = rs.getInt("id_articulo");
        int cantidad = rs.getInt("cantidad");
        LocalDateTime fechaHora = rs.getTimestamp("fecha_hora").toLocalDateTime();
        String estado = rs.getString("estado");

        return new Pedido(idPedido, idCliente, idArticulo, cantidad, fechaHora, estado);
    }

    // =========================================================
    // MÉTODOS PRINCIPALES DE PEDIDOS
    // =========================================================

    @Override
    public void insertar(Pedido pedido) throws DAOException {
        String sql = "INSERT INTO pedidos (id_cliente, id_articulo, cantidad, fecha_hora, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.getConexion()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, pedido.getIdCliente());
                ps.setInt(2, pedido.getIdArticulo());
                ps.setInt(3, pedido.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(pedido.getFechaHora()));
                ps.setString(5, pedido.getEstado());

                ps.executeUpdate();
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al insertar el pedido. Se hizo ROLLBACK.", e);
            } finally {
                conexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DAOException("Error de conexión a la base de datos", e);
        }
    }

    @Override
    public void eliminar(int id) throws DAOException {
        String sql = "{call sp_cancelar_pedido(?, ?)}";

        try (Connection conexion = ConexionBD.getConexion();
             CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.BOOLEAN);
            cs.execute();

            if (!cs.getBoolean(2)) {
                throw new DAOException("No se puede cancelar el pedido: el tiempo de preparación ha expirado.");
            }
        } catch (SQLException e) {
            throw new DAOException("Error al intentar cancelar el pedido en la BD", e);
        }
    }


    public List<Pedido> obtenerPedidosPendientes(int idCliente) throws DAOException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM pedidos p JOIN articulos a ON p.id_articulo = a.id_articulo " +
                "WHERE TIMESTAMPDIFF(MINUTE, p.fecha_hora, NOW()) <= a.tiempo_preparacion";

        if (idCliente > 0) { sql += " AND p.id_cliente = ?"; }

        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (idCliente > 0) { ps.setInt(1, idCliente); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { lista.add(mapearPedido(rs)); }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener pedidos pendientes", e);
        }
        return lista;
    }

    public List<Pedido> obtenerPedidosEnviados(int idCliente) throws DAOException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM pedidos p JOIN articulos a ON p.id_articulo = a.id_articulo " +
                "WHERE TIMESTAMPDIFF(MINUTE, p.fecha_hora, NOW()) > a.tiempo_preparacion";

        if (idCliente > 0) { sql += " AND p.id_cliente = ?"; }

        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (idCliente > 0) { ps.setInt(1, idCliente); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { lista.add(mapearPedido(rs)); }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener pedidos enviados", e);
        }
        return lista;
    }

    // =========================================================
    // MÉTODOS RELLENO PARA LA INTERFAZ
    // =========================================================

    @Override
    public Pedido obtenerPorId(int id) throws DAOException {
        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";
        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearPedido(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener pedido por ID", e);
        }
        return null;
    }

    @Override
    public List<Pedido> obtenerTodos() throws DAOException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";
        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapearPedido(rs));
        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los pedidos", e);
        }
        return lista;
    }

    @Override
    public List<Pedido> obtenerPorCliente(int idCliente) throws DAOException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE id_cliente = ?";
        try (Connection conexion = ConexionBD.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener pedidos del cliente", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Pedido pedido) throws DAOException {
        throw new DAOException("La actualización de pedidos no aplica.");
    }
}