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

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Controlador {

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private ArticuloDAO articuloDAO;

    public Controlador() {
        try {
            DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
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

        if (cantidad <= 0) {
            throw new DAOException("La cantidad del pedido debe ser mayor que 0.", new SQLException());
        }

        Cliente cliente = clienteDAO.buscarPorEmail(email);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("Cliente", email);
        }

        Articulo articulo = articuloDAO.obtenerPorId(codigoArticulo);
        if (articulo == null) {
            throw new RecursoNoEncontradoException("Artículo", codigoArticulo);
        }

        if (articulo.getCantidadDisponible() < cantidad) {
            throw new DAOException(
                    "Stock insuficiente. Disponible: " + articulo.getCantidadDisponible() + " unidades.",
                    new SQLException()
            );
        }

        Pedido nuevoPedido = new Pedido(0, cliente, articulo, cantidad, LocalDateTime.now(), "PENDIENTE");
        pedidoDAO.insertar(nuevoPedido);

        return nuevoPedido;
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

    public List<Pedido> obtenerPedidosPendientes(String email)
            throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {
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

    public void cambiarEstadoPedido(int idPedido, String nuevoEstado)
            throws DAOException, RecursoNoEncontradoException, CambioEstadoPedidoNoPermitidoException {

        sincronizarEstadosAutomaticos();
        validarEstadoPedido(nuevoEstado);

        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido == null) {
            throw new RecursoNoEncontradoException("Pedido", String.valueOf(idPedido));
        }

        String estadoActual = pedido.getEstado().toUpperCase();
        String estadoNuevo = nuevoEstado.toUpperCase();

        if (estadoActual.equals(estadoNuevo)) {
            throw new CambioEstadoPedidoNoPermitidoException(
                    "El pedido con número " + idPedido + " ya se encuentra en estado '" + estadoActual + "'."
            );
        }

        if (estadoActual.equals("ENVIADO") && estadoNuevo.equals("PENDIENTE")) {
            throw new CambioEstadoPedidoNoPermitidoException(idPedido, estadoActual, estadoNuevo);
        }

        pedidoDAO.actualizarEstado(idPedido, estadoNuevo);
    }

    private void sincronizarEstadosAutomaticos() throws DAOException {
        List<Pedido> pedidos = pedidoDAO.obtenerTodos();

        for (Pedido pedido : pedidos) {
            if (pedido.debeMarcarseComoEnviadoAutomaticamente()) {
                if (!"ENVIADO".equalsIgnoreCase(pedido.getEstado())) {
                    pedidoDAO.actualizarEstado(pedido.getNumeroPedido(), "ENVIADO");
                }
            }
        }
    }

    private void validarEstadoPedido(String nuevoEstado) throws DAOException {
        if (!"PENDIENTE".equalsIgnoreCase(nuevoEstado) && !"ENVIADO".equalsIgnoreCase(nuevoEstado)) {
            throw new DAOException("Estado no válido. Solo se permite PENDIENTE o ENVIADO.", new SQLException());
        }
    }

    public Cliente anadirCliente(String email, String nombre, String domicilio, String nif, int tipo)
            throws EmailInvalidoException, DAOException, TipoClienteInvalidoException {

        emailValido(email);

        Cliente nuevoCliente;
        if (tipo == 2) {
            nuevoCliente = new ClientePremium(email, nombre, domicilio, nif);
        } else if (tipo == 1) {
            nuevoCliente = new ClienteEstandar(email, nombre, domicilio, nif);
        } else {
            throw new TipoClienteInvalidoException("El tipo de cliente debe ser 1 (Estándar) o 2 (Premium)");
        }

        clienteDAO.insertar(nuevoCliente);

        return nuevoCliente;
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

    public List<Cliente> obtenerClientesPremium() throws DAOException {
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

    public void anadirArticulo(String codigo, String descripcion, double precioVenta,
                               double gastosEnvio, int tiempoPreparacionMin, int cantidadDisponible)
            throws DAOException {

        if (articuloDAO.obtenerPorId(codigo) != null) {
            throw new DAOException("Operación cancelada: El artículo con código '" + codigo + "' ya existe.");
        }

        if (cantidadDisponible < 0) {
            throw new DAOException("La cantidad disponible no puede ser negativa.", new SQLException());
        }

        Articulo articulo = new Articulo(
                codigo,
                descripcion,
                precioVenta,
                gastosEnvio,
                tiempoPreparacionMin,
                cantidadDisponible
        );

        articuloDAO.insertar(articulo);
    }

    public void sumarStockArticulo(String codigo, int cantidad) throws DAOException, RecursoNoEncontradoException {
        Articulo articulo = articuloDAO.obtenerPorId(codigo);

        if (articulo == null) {
            throw new RecursoNoEncontradoException("Articulo", codigo);
        }

        if (cantidad <= 0) {
            throw new DAOException("La cantidad a sumar debe ser mayor que 0.", new SQLException());
        }

        articuloDAO.sumarStock(codigo, cantidad);
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
}