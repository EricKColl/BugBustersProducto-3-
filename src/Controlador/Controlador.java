package Controlador;

import DAO.Interfaces.ArticuloDAO;
import DAO.Interfaces.ClienteDAO;
import DAO.Interfaces.PedidoDAO;
import Factory.DAOFactory;
import Modelo.Articulo;
import Modelo.Cliente;
import Modelo.ClienteEstandar;
import Modelo.ClientePremium;
import Modelo.Pedido;
import Excepciones.DAOException;
import Modelo.Excepciones.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Controlador {

    private DAOFactory factory;
    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private ArticuloDAO articuloDAO;

    public Controlador() {
        try {
            this.factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
            this.clienteDAO = factory.getClienteDAO();
            this.articuloDAO = factory.getArticuloDAO();
            this.pedidoDAO = factory.getPedidoDAO();
        } catch (DAOException e) {
            throw new RuntimeException("Error fatal: No se pudo conectar con la base de datos.", e);
        }
    }

    public Pedido anadirPedido(String email, String codigoArticulo, int cantidad)
            throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {

        emailValido(email);

        Cliente cliente = clienteDAO.buscarPorEmail(email);
        if (cliente == null) throw new RecursoNoEncontradoException("Cliente", email);

        Articulo articulo = articuloDAO.obtenerPorId(codigoArticulo);
        if (articulo == null) throw new RecursoNoEncontradoException("Articulo", codigoArticulo);

        Connection repo = null;
        try {
            repo = factory.getConnection();
            repo.setAutoCommit(false);

            Pedido pedido = new Pedido(
                    0,
                    cliente,
                    articulo,
                    cantidad,
                    LocalDateTime.now(),
                    "PENDIENTE"
            );

            pedidoDAO.insertar(pedido);

            repo.commit();
            return pedido;
        } catch (SQLException | DAOException e) {
            try {
                if (repo != null) repo.rollback();
            } catch (SQLException ex) { /* ... */ }
            throw new DAOException("No se pudo completar el pedido: " + e.getMessage());
        } finally {
            restaurarAutoCommit(repo);
        }
    }

    public void eliminarPedido(int idPedido) throws DAOException, RecursoNoEncontradoException, PedidoNoCancelableException {
        sincronizarEstadosAutomaticos();

        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);

        if (pedido == null) {
            throw new RecursoNoEncontradoException("Pedido", String.valueOf(idPedido));
        }

        if (!pedido.puedeCancelar()) {
            throw new PedidoNoCancelableException(idPedido);
        }

        pedidoDAO.eliminar(idPedido);
    }

    public List<Pedido> obtenerPedidosPendientes(String email) throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {
        sincronizarEstadosAutomaticos();
        int idFiltro = 0;

        if (email != null && !email.trim().isEmpty()) {
            emailValido(email);
            Cliente c = buscarCliente(email);
            idFiltro = c.getIdCliente();

            if (idFiltro <= 0) {
                throw new DAOException("El cliente encontrado tiene un ID interno no válido.", new SQLException());
            }
        }

        return pedidoDAO.obtenerPedidosPendientes(idFiltro);
    }

    public List<Pedido> obtenerPedidosEnviados(String email)
            throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {
        sincronizarEstadosAutomaticos();
        int idFiltro = 0;

        if (email != null && !email.trim().isEmpty()) {
            emailValido(email);
            Cliente c = buscarCliente(email);
            idFiltro = c.getIdCliente();

            if (idFiltro <= 0) {
                throw new DAOException("Error de integridad: El ID del cliente no es válido.", new SQLException());
            }
        }

        return pedidoDAO.obtenerPedidosEnviados(idFiltro);
    }

    public void marcarComoEnviado(int idPedido) throws DAOException, RecursoNoEncontradoException {
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);

        if (pedido == null) {
            throw new RecursoNoEncontradoException("Pedido", String.valueOf(idPedido));
        }

        if ("ENVIADO".equalsIgnoreCase(pedido.getEstado())) {
            throw new DAOException("El pedido #" + idPedido + " ya consta como ENVIADO.");
        }

        pedidoDAO.actualizarEstado(idPedido, "ENVIADO");
    }

    private void sincronizarEstadosAutomaticos() throws DAOException {
        List<Pedido> pedidos = pedidoDAO.obtenerTodos();

        for (Pedido pedido : pedidos) {
            if ("PENDIENTE".equalsIgnoreCase(pedido.getEstado())) {
                if (pedido.debeMarcarseComoEnviadoAutomaticamente()) {
                    pedidoDAO.actualizarEstado(pedido.getNumeroPedido(), "ENVIADO");
                    pedido.setEstado("ENVIADO");
                }
            }
        }
    }

    private void validarEstadoPedido(String nuevoEstado) throws DAOException {
        if (!"PENDIENTE".equalsIgnoreCase(nuevoEstado) && !"ENVIADO".equalsIgnoreCase(nuevoEstado)) {
            throw new DAOException("Estado no válido. Solo se permite PENDIENTE o ENVIADO.", new SQLException());
        }
    }

    public Cliente anadirCliente(String email, String nombre, String domicilio, String nif, int tipoCliente)
            throws DAOException {

        Connection repo = null;
        try {
            repo = factory.getConnection();
            repo.setAutoCommit(false);

            Cliente cliente;
            if (tipoCliente == 2) {
                cliente = new ClientePremium(email, nombre, domicilio, nif);
            } else {
                cliente = new ClienteEstandar(email, nombre, domicilio, nif);
            }

            clienteDAO.insertar(cliente);

            repo.commit();
            return cliente;
        } catch (SQLException | DAOException e) {
            if (repo != null) try { repo.rollback(); } catch (SQLException ex) {}
            throw new DAOException("Error al añadir cliente: " + e.getMessage());
        } finally {
            restaurarAutoCommit(repo);
        }
    }

    public void existeCliente(String email) throws DAOException {
        if (clienteDAO.existePorEmail(email)) {
            throw new DAOException("El email '" + email + "' ya está registrado.");
        }
    }

    public List<Cliente> obtenerTodosClientes() throws DAOException {
        return clienteDAO.obtenerTodos();
    }

    public List<Cliente> obtenerClientesEstandar() throws DAOException {
        return clienteDAO.obtenerClientesEstandar();
    }

    public List<Cliente> obtenerClientesPremium() throws DAOException{
        return clienteDAO.obtenerClientesPremium();
    }

    public void emailValido(String email) throws EmailInvalidoException {
        if (!email.contains("@") && !email.contains(".")) {
            throw new EmailInvalidoException(email);
        }
    }

    public Cliente buscarCliente(String email) throws EmailInvalidoException, RecursoNoEncontradoException, DAOException {
        emailValido(email);

        Cliente cliente = clienteDAO.buscarPorEmail(email);

        if (cliente == null) {
            throw new RecursoNoEncontradoException("cliente", email);
        }

        return cliente;
    }

    public void eliminarCliente(Cliente cliente) throws DAOException {
        if (cliente != null) {
            clienteDAO.eliminar(cliente.getIdCliente());
        }
    }

    public Articulo buscarArticulo(String codigo) throws RecursoNoEncontradoException, DAOException {
        Articulo articulo = articuloDAO.obtenerPorId(codigo);
        if (articulo == null) {
            throw new RecursoNoEncontradoException("Articulo", codigo);
        }
        return articulo;
    }

    public Articulo anadirArticulo(String codigo, String descripcion, double precioVenta,
                                   double gastosEnvio, int tiempoPreparacionMin)
            throws DAOException {

        // Validación previa para no intentar insertar si ya existe
        if (articuloDAO.obtenerPorId(codigo) != null) {
            throw new DAOException("El artículo con código '" + codigo + "' ya existe.");
        }

        Connection repo = null;
        try {
            repo = factory.getConnection();
            repo.setAutoCommit(false);

            Articulo articulo = new Articulo(codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin);

            articuloDAO.insertar(articulo);

            repo.commit();
            return articulo;
        } catch (SQLException | DAOException e) {
            try {
                if (repo != null) repo.rollback();
            } catch (SQLException ex) { /* ... */ }
            throw new DAOException("Error al añadir artículo: " + e.getMessage());
        } finally {
            restaurarAutoCommit(repo);
        }
    }

    public void eliminarArticulo(String codigo) throws RecursoNoEncontradoException, DAOException {
        Articulo articulo = articuloDAO.obtenerPorId(codigo);
        if (articulo == null) {
            throw new RecursoNoEncontradoException("Articulo", codigo);
        }

        articuloDAO.eliminar(codigo);
    }

    public List<Articulo> obtenerTodosArticulos() throws DAOException {
        return articuloDAO.obtenerTodos();
    }

    private void restaurarAutoCommit(Connection repo) {
        try {
            if (repo != null) {
                repo.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error al restaurar AutoCommit: " + e.getMessage());
        }
    }
}