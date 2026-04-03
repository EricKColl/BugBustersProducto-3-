package DAO.MySQL;

import DAO.Interfaces.ArticuloDAO;
import DAO.Interfaces.ClienteDAO;
import DAO.Interfaces.PedidoDAO;
import Excepciones.DAOException;
import Factory.DAOFactory;
import Modelo.Articulo;
import Modelo.Cliente;
import Modelo.Pedido;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOMySQL implements PedidoDAO {

    private final Connection conexion;

    public PedidoDAOMySQL(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Pedido pedido) throws DAOException {
        String sql = "INSERT INTO pedidos (id_cliente, id_articulo, cantidad, fecha_hora, estado) VALUES (?, ?, ?, ?, ?)";

        boolean autoCommitAnterior;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int idCliente = pedido.getCliente().getIdCliente();
                int idArticulo = obtenerIdArticuloPorCodigo(pedido.getArticulo().getCodigo());
                LocalDateTime fechaActual = LocalDateTime.now();

                ps.setInt(1, idCliente);
                ps.setInt(2, idArticulo);
                ps.setInt(3, pedido.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(fechaActual));
                ps.setString(5, "PENDIENTE");

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    conexion.rollback();
                    throw new DAOException("No se pudo insertar el pedido en la base de datos.", new SQLException());
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setIdPedido(rs.getInt(1));
                    }
                }

                pedido.setFechaHora(fechaActual);
                pedido.setEstado("PENDIENTE");

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al insertar el pedido en MySQL", e);
            } catch (DAOException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al gestionar la transacción al insertar pedido", e);
        }
    }

    private int obtenerIdArticuloPorCodigo(String codigoArticulo) throws DAOException {
        String sql = "SELECT id_articulo FROM articulos WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigoArticulo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_articulo");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener el id del artículo con código: " + codigoArticulo, e);
        }

        throw new DAOException("No existe ningún artículo con código: " + codigoArticulo, new SQLException());
    }

    private String obtenerCodigoArticuloPorId(int idArticulo) throws DAOException {
        String sql = "SELECT codigo FROM articulos WHERE id_articulo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idArticulo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("codigo");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener el código del artículo con id: " + idArticulo, e);
        }

        throw new DAOException("No existe ningún artículo con id: " + idArticulo, new SQLException());
    }

    @Override
    public void eliminar(Integer idPedido) throws DAOException { // 1. Añadimos el throws
        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";

        boolean autoCommitAnterior;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idPedido);
                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new DAOException("No se encontró el pedido con ID: " + idPedido);
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al eliminar el pedido de la base de datos.", e);
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error crítico en la gestión de la base de datos.", e);
        }
    }

    @Override
    public List<Pedido> obtenerTodos() throws DAOException {
        return cargarPedidos(null, null);
    }

    @Override
    public List<Pedido> obtenerPedidosPendientes(int idCliente) throws DAOException {
        return cargarPedidos(idCliente > 0 ? idCliente : null, "PENDIENTE");
    }

    @Override
    public List<Pedido> obtenerPedidosEnviados(int idCliente) throws DAOException {
        return cargarPedidos(idCliente > 0 ? idCliente : null, "ENVIADO");
    }

    private List<Pedido> cargarPedidos(Integer idClienteFiltro, String estadoFiltro) throws DAOException {
        List<Pedido> lista = new ArrayList<>();

        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = factory.getClienteDAO();
        ArticuloDAO articuloDAO = factory.getArticuloDAO();

        StringBuilder sql = new StringBuilder("SELECT * FROM pedidos");
        List<Object> parametros = new ArrayList<>();
        boolean tieneWhere = false;

        if (estadoFiltro != null) {
            sql.append(" WHERE estado = ?");
            parametros.add(estadoFiltro);
            tieneWhere = true;
        }

        if (idClienteFiltro != null) {
            sql.append(tieneWhere ? " AND" : " WHERE");
            sql.append(" id_cliente = ?");
            parametros.add(idClienteFiltro);
        }

        sql.append(" ORDER BY id_pedido");

        try (PreparedStatement ps = conexion.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                Object valor = parametros.get(i);
                if (valor instanceof String) {
                    ps.setString(i + 1, (String) valor);
                } else {
                    ps.setInt(i + 1, (Integer) valor);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idPedido = rs.getInt("id_pedido");
                    int idCliente = rs.getInt("id_cliente");
                    int idArticulo = rs.getInt("id_articulo");
                    int cantidad = rs.getInt("cantidad");
                    LocalDateTime fechaHora = rs.getTimestamp("fecha_hora").toLocalDateTime();
                    String estado = rs.getString("estado");

                    Cliente cliente = clienteDAO.obtenerPorId(idCliente);
                    String codigoArticulo = obtenerCodigoArticuloPorId(idArticulo);
                    Articulo articulo = articuloDAO.obtenerPorId(codigoArticulo);

                    if (cliente != null && articulo != null) {
                        Pedido pedido = new Pedido(idPedido, cliente, articulo, cantidad, fechaHora, estado);
                        lista.add(pedido);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener pedidos desde MySQL", e);
        }

        return lista;
    }

    @Override
    public Pedido obtenerPorId(Integer id) throws DAOException {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = factory.getClienteDAO();
        ArticuloDAO articuloDAO = factory.getArticuloDAO();

        String sql = "SELECT * FROM pedidos WHERE id_pedido = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    int idArticulo = rs.getInt("id_articulo");
                    int cantidad = rs.getInt("cantidad");
                    LocalDateTime fechaHora = rs.getTimestamp("fecha_hora").toLocalDateTime();
                    String estado = rs.getString("estado");

                    Cliente cliente = clienteDAO.obtenerPorId(idCliente);
                    String codigoArticulo = obtenerCodigoArticuloPorId(idArticulo);
                    Articulo articulo = articuloDAO.obtenerPorId(codigoArticulo);

                    if (cliente != null && articulo != null) {
                        return new Pedido(id, cliente, articulo, cantidad, fechaHora, estado);
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Error al obtener el pedido con id: " + id, e);
        }

        return null;
    }

    public void actualizar(Pedido pedido) throws DAOException {
        String sql = "UPDATE pedidos SET id_cliente = ?, id_articulo = ?, cantidad = ?, fecha_hora = ?, estado = ? WHERE id_pedido = ?";

        boolean autoCommitAnterior;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                int idCliente = pedido.getCliente().getIdCliente();
                int idArticulo = obtenerIdArticuloPorCodigo(pedido.getArticulo().getCodigo());

                ps.setInt(1, idCliente);
                ps.setInt(2, idArticulo);
                ps.setInt(3, pedido.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(pedido.getFechaHora()));
                ps.setString(5, pedido.getEstado());
                ps.setInt(6, pedido.getNumeroPedido());

                ps.executeUpdate();
                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al actualizar el pedido en MySQL", e);
            } catch (DAOException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al gestionar la transacción al actualizar pedido", e);
        }
    }

    @Override
    public void actualizarEstado(int idPedido, String nuevoEstado) throws DAOException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

        boolean autoCommitAnterior;
        try {
            autoCommitAnterior = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idPedido);
                ps.executeUpdate();
                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw new DAOException("Error al actualizar el estado del pedido en MySQL", e);
            } finally {
                conexion.setAutoCommit(autoCommitAnterior);
            }

        } catch (SQLException e) {
            throw new DAOException("Error al gestionar la transacción al actualizar el estado del pedido", e);
        }
    }
}