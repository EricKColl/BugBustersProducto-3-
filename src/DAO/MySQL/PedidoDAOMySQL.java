package DAO.MySQL;

import DAO.Interfaces.PedidoDAO;
import DAO.Interfaces.ClienteDAO;
import DAO.Interfaces.ArticuloDAO;
import Factory.DAOFactory;
import Modelo.Pedido;
import Modelo.Cliente;
import Modelo.Articulo;
import Excepciones.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOMySQL implements PedidoDAO {

    private final Connection conexion;

    public PedidoDAOMySQL(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Pedido pedido) throws DAOException {
        // El SP insertar_pedido espera los IDs (FK) de las tablas
        String sql = "{CALL insertar_pedido(?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            // Extraemos el Email del cliente y el Código del artículo como identificadores
            cs.setInt(1, pedido.getCliente().getIdCliente());
            cs.setString(2, pedido.getArticulo().getCodigo());
            cs.setInt(3, pedido.getCantidad());
            cs.setString(4, "PENDIENTE");

            cs.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al insertar el pedido en MySQL", e);
        }
    }

    @Override
    public void eliminar(Integer idPedido) { // <-- Le quitamos el throws DAOException
        String sql = "{CALL eliminar_pedido(?)}";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idPedido);
            cs.executeUpdate();
        } catch (SQLException e) {
            // Como la interfaz genérica no nos deja lanzar la excepción, la imprimimos
            System.err.println("Error al eliminar pedido: " + e.getMessage());
        }
    }

    @Override
    public List<Pedido> obtenerTodos() throws DAOException {
        return ejecutarConsulta("SELECT p.* FROM pedidos p", null);
    }

    @Override
    public List<Pedido> obtenerPedidosPendientes(int idUnused) throws DAOException { // <-- Volvemos al int
        String sql = "SELECT p.* FROM pedidos p JOIN articulos a ON p.id_articulo = a.id_articulo " +
                "WHERE TIMESTAMPDIFF(MINUTE, p.fecha_hora, NOW()) <= a.tiempo_preparacion";
        // Pasamos null como filtroEmail porque el int no nos sirve para filtrar por correo
        return ejecutarConsulta(sql, null);
    }

    @Override
    public List<Pedido> obtenerPedidosEnviados(int idUnused) throws DAOException { // <-- Volvemos al int
        String sql = "SELECT p.* FROM pedidos p JOIN articulos a ON p.id_articulo = a.id_articulo " +
                "WHERE TIMESTAMPDIFF(MINUTE, p.fecha_hora, NOW()) > a.tiempo_preparacion";
        return ejecutarConsulta(sql, null);
    }

    /* =========================================================
       MÉTODO DE CONSULTA Y MAPEADO (Con SQL Dinámico)
       ========================================================= */

    private List<Pedido> ejecutarConsulta(String sqlBase, String filtroEmail) throws DAOException {
        List<Pedido> lista = new ArrayList<>();

        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        ClienteDAO clienteDAO = factory.getClienteDAO();
        ArticuloDAO articuloDAO = factory.getArticuloDAO();

        // 1. CONSTRUIMOS LA CONSULTA DINÁMICAMENTE
        StringBuilder sql = new StringBuilder(sqlBase);
        boolean tieneFiltro = (filtroEmail != null && !filtroEmail.trim().isEmpty());

        if (tieneFiltro) {
            if (sqlBase.toUpperCase().contains("WHERE")) {
                sql.append(" AND p.id_cliente = ?"); // Si ya hay un WHERE (como en pendientes/enviados), añadimos AND
            } else {
                sql.append(" WHERE p.id_cliente = ?"); // Si no lo hay (como en obtenerTodos), añadimos el WHERE
            }
        }

        // 2. PREPARAMOS Y EJECUTAMOS
        try (PreparedStatement stat = conexion.prepareStatement(sql.toString())) {

            // Solo metemos la interrogación si realmente hay filtro
            if (tieneFiltro) {
                stat.setString(1, filtroEmail);
            }

            try (ResultSet rs = stat.executeQuery()) {
                while (rs.next()) {
                    int idCli = rs.getInt("id_cliente");
                    String codArt = rs.getString("id_articulo");

                    // REHIDRATACIÓN: Buscamos los objetos reales
                    Cliente c = clienteDAO.obtenerPorId(idCli);
                    Articulo a = articuloDAO.obtenerPorId(codArt);

                    if (c != null && a != null) {
                        Pedido p = new Pedido(
                                rs.getInt("id_pedido"),
                                c,
                                a,
                                rs.getInt("cantidad"),
                                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                                rs.getString("estado")
                        );
                        lista.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al mapear la lista de pedidos desde MySQL", e);
        }
        return lista;
    }

    @Override
    public Pedido obtenerPorId(Integer id) throws DAOException { return null; }

    @Override
    public void actualizar(Pedido pedido) throws DAOException { }
}