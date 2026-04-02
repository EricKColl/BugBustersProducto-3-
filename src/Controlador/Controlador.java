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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Controlador que actúa como puente entre la Vista y el Modelo.
 *
 * En el patrón MVC (Modelo-Vista-Controlador), esta clase es el intermediario
 * que procesa las solicitudes de la vista, interactúa con el modelo (Datos)
 * y devuelve los resultados. La vista nunca accede directamente al modelo,
 * solo se comunica a través del controlador.
 *
 * El controlador se encarga de:
 * <ul>
 *   <li>Recibir y validar los datos provenientes de la vista</li>
 *   <li>Crear los objetos del modelo (Artículo, Cliente, Pedido)</li>
 *   <li>Invocar los métodos correspondientes en la capa de DAO</li>
 *   <li>Manejar las excepciones y transformarlas cuando sea necesario</li>
 *   <li>Devolver los resultados a la vista para su presentación</li>
 * </ul>
 *
 * @author BugBusters
 * @version 2.0
 * @since 1.0
 */

public class Controlador {

    private PedidoDAO pedidoDAO;
    private ClienteDAO clienteDAO;
    private ArticuloDAO articuloDAO;

    public Controlador() {
        try {
            // Intentamos conectar con la factoría MySQL
            DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);

            // Asignamos los DAOs
            this.clienteDAO = factory.getClienteDAO();
            this.articuloDAO = factory.getArticuloDAO();
            this.pedidoDAO = factory.getPedidoDAO();

        } catch (DAOException e) {
            // Si la base de datos falla, informamos y tomamos una decisión
            System.err.println("Error crítico al inicializar los DAOs: " + e.getMessage());

            // Opción recomendada: lanzar una RuntimeException.
            // Esto detiene el programa porque sin DAOs el controlador no sirve para nada.
            throw new RuntimeException("No se pudo arrancar el sistema de persistencia.", e);
        }
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE PEDIDOS
    // ==========================================

    /**
     * Añade un nuevo pedido al sistema validando la existencia del cliente y el artículo.
     * * @param email Email del cliente que realiza el pedido.
     * @param codigoArticulo Código del artículo solicitado.
     * @param cantidad Cantidad de unidades del artículo.
     * @return El objeto Pedido recién creado y persistido.
     */
    public Pedido anadirPedido(String email, String codigoArticulo, int cantidad)
            throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {

        emailValido(email);

        Cliente cliente = clienteDAO.obtenerPorEmail(email);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("Cliente", email);
        }

        Articulo articulo = articuloDAO.obtenerPorId(codigoArticulo);
        if (articulo == null) {
            throw new RecursoNoEncontradoException("Artículo", codigoArticulo);
        }

        // El ID es 0 porque la base de datos (MySQL) se encarga del autoincremento
        Pedido nuevoPedido = new Pedido(0, cliente, articulo, cantidad, LocalDateTime.now(), "PENDIENTE");
        pedidoDAO.insertar(nuevoPedido);

        return nuevoPedido;
    }

    /**
     * Elimina (cancela) un pedido del sistema si las reglas de negocio lo permiten.
     * * @param idPedido Identificador único del pedido.
     */
    public void eliminarPedido(int idPedido) throws DAOException, RecursoNoEncontradoException, PedidoNoCancelableException {
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);

        if (pedido == null) {
            throw new RecursoNoEncontradoException("Pedido", String.valueOf(idPedido));
        }

        if (!pedido.puedeCancelar()) {
            throw new PedidoNoCancelableException(idPedido);
        }

        pedidoDAO.eliminar(idPedido);
    }

    /**
     * Obtiene la lista de pedidos pendientes, opcionalmente filtrada por cliente.
     * * @param email Filtro de email (String). Si es nulo o vacío, devuelve todos.
     * @return Lista de objetos Pedido pendientes.
     */
    public List<Pedido> obtenerPedidosPendientes(String email) throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {
        int idFiltro = 0;

        if (email != null && !email.trim().isEmpty()) {
            emailValido(email);
            Cliente c = buscarCliente(email);

            idFiltro = c.getIdCliente();

            if (idFiltro <= 0) {
                throw new DAOException("El cliente encontrado tiene un ID interno no válido.", null);
            }
        }

        return pedidoDAO.obtenerPedidosPendientes(idFiltro);
    }

    /**
     * Obtiene la lista de pedidos ya enviados, opcionalmente filtrada por cliente.
     * * @param email Filtro de email (String). Si es nulo o vacío, devuelve todos.
     * @return Lista de objetos Pedido enviados.
     */
    public List<Pedido> obtenerPedidosEnviados(String email)
            throws DAOException, RecursoNoEncontradoException, EmailInvalidoException {

        int idFiltro = 0;

        if (email != null && !email.trim().isEmpty()) {
            emailValido(email);
            Cliente c = buscarCliente(email); // buscarCliente ya lanza DAOException si hay error en BD

            idFiltro = c.getIdCliente();

            // Validación lógica de seguridad (opcional pero recomendada)
            if (idFiltro <= 0) {
                throw new DAOException("Error de integridad: El ID del cliente no es válido.", null);
            }
        }

        // Si pedidoDAO falla, lanzará su propia DAOException hacia la Vista
        return pedidoDAO.obtenerPedidosEnviados(idFiltro);
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE CLIENTES
    // ==========================================

    public Cliente anadirCliente(String email, String nombre, String domicilio, String nif, int tipoCliente)
            throws EmailInvalidoException, TipoClienteInvalidoException, YaExisteException, DAOException {

        // 1. Validaciones previas
        if (clienteDAO.existePorEmail(email)) {
            throw new YaExisteException("cliente", email);
        }

        Cliente nuevoCliente;
        if (tipoCliente == 1) {
            nuevoCliente = new ClienteEstandar(email, nombre, domicilio, nif);
        } else {
            nuevoCliente = new ClientePremium(email, nombre, domicilio, nif);
        }

        // 2. Insertar
        clienteDAO.insertar(nuevoCliente);

        // 3. Devolver el cliente recuperado de la DB (con su ID real)
        return clienteDAO.obtenerPorEmail(email);
    }

    public List<Cliente> obtenerTodosClientes() throws DAOException {
        return clienteDAO.obtenerTodos();
    }

    public List<Cliente> obtenerClientesEstandar() {
        return clienteDAO.obtenerClientesEstandar();
    }

    public List<Cliente> obtenerClientesPremium() {
        return clienteDAO.obtenerClientesPremium();
    }

    public void emailValido(String email) throws EmailInvalidoException {
        if (!email.contains("@") || !email.contains(".")) {
            throw new EmailInvalidoException(email);
        }
    }

    public Cliente buscarCliente(String email) throws EmailInvalidoException, RecursoNoEncontradoException, DAOException {
        // 1. Primero validamos el formato (reutilizamos el método de antes)
        emailValido(email);

        // 2. Buscamos en la base de datos a través del DAO
        Cliente cliente = clienteDAO.obtenerPorEmail(email);

        // 3. Si no existe, lanzamos la excepción para que la Vista sepa qué decir
        if (cliente == null) {
            throw new RecursoNoEncontradoException("cliente", email);
        }

        return cliente;
    }

    public void eliminarCliente(String email) throws EmailInvalidoException, RecursoNoEncontradoException, DAOException {
        // 1. Validamos formato
        emailValido(email);

        // 2. Buscamos al cliente para obtener su ID y verificar su existencia
        Cliente cliente = clienteDAO.obtenerPorEmail(email);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("cliente", email);
        }

        // 3. Extraemos su ID numérico y usamos el método genérico que pide un Integer
        clienteDAO.eliminar(cliente.getIdCliente());
    }

/* =========================================================
       ================= GESTIÓN DE ARTÍCULOS ==================
       ========================================================= */

    /**
     * Busca un artículo por su código.
     *
     * @param codigo Código del artículo a buscar
     * @return El objeto Artículo si existe
     * @throws RecursoNoEncontradoException Si no existe un artículo con ese código
     */
    public Articulo buscarArticulo(String codigo) throws RecursoNoEncontradoException, DAOException {
        Articulo articulo = articuloDAO.obtenerPorId(codigo);
        if (articulo == null) {
            throw new RecursoNoEncontradoException("Articulo", codigo);
        }
        return articulo;
    }

    /**
     * Añade un nuevo artículo al sistema.
     *
     * @param codigo                   Código único identificador del artículo
     * @param descripcion          Descripción textual del artículo
     * @param precioVenta          Precio de venta del artículo en euros
     * @param gastosEnvio          Gastos de envío asociados al artículo
     * @param tiempoPreparacionMin Tiempo de preparación en minutos
     * @throws YaExisteException Si ya existe un artículo con el mismo código
     */
    public void anadirArticulo(String codigo, String descripcion, double precioVenta,
                               double gastosEnvio, int tiempoPreparacionMin)
            throws YaExisteException, DAOException {

        if (articuloDAO.obtenerPorId(codigo) != null) {
            throw new YaExisteException("artículo", codigo);
        }

        Articulo articulo = new Articulo(codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin);
        articuloDAO.insertar(articulo);
    }

    /**
     * Obtiene una lista con todos los artículos almacenados.
     *
     * @return Lista de todos los artículos
     */
    public List<Articulo> obtenerTodosArticulos() throws DAOException {
        return articuloDAO.obtenerTodos();
    }
}