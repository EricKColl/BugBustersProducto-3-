package Controlador;

import DAO.Interfaces.PedidoDAO;
import DAO.Interfaces.ClienteDAO;
import DAO.Interfaces.ArticuloDAO;
import Factory.DAOFactory;
import Modelo.Pedido;
import Modelo.Cliente;
import Modelo.Articulo;
import Modelo.ClienteEstandar;
import Modelo.ClientePremium;
import Excepciones.DAOException;
import Modelo.Excepciones.*;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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

    public void añadirPedido(Pedido pedido) {
        try {
            pedidoDAO.insertar(pedido);
            System.out.println("Pedido guardado correctamente en la base de datos.");
        } catch (DAOException e) {
            System.err.println("Error al guardar el pedido: " + e.getMessage());
        }
    }

    public void eliminarPedido(int idPedido) {
        pedidoDAO.eliminar(idPedido);
        System.out.println("El pedido " + idPedido + " ha sido cancelado y eliminado con éxito.");
    }

    public void mostrarPedidosPendientes(int idCliente) {
        try {
            List<Pedido> pendientes = pedidoDAO.obtenerPedidosPendientes(idCliente);
            if (pendientes.isEmpty()) {
                System.out.println("No hay pedidos pendientes de envío.");
            } else {
                System.out.println("\n--- PEDIDOS PENDIENTES ---");
                for (Pedido p : pendientes) {
                    double total = calcularTotalPedido(p);
                    System.out.println(p.toString() + "TOTAL a pagar: " + String.format("%.2f", total) + "€");
                }
            }
        } catch (DAOException e) {
            System.err.println("❌ Error al recuperar los pedidos pendientes: " + e.getMessage());
        }
    }

    public void mostrarPedidosEnviados(int idCliente) {
        try {
            List<Pedido> enviados = pedidoDAO.obtenerPedidosEnviados(idCliente);
            if (enviados.isEmpty()) {
                System.out.println("No hay pedidos enviados.");
            } else {
                System.out.println("\n--- PEDIDOS ENVIADOS ---");
                for (Pedido p : enviados) {
                    double total = calcularTotalPedido(p);
                    System.out.println(p.toString() + "TOTAL pagado: " + String.format("%.2f", total) + "€");
                }
            }
        } catch (DAOException e) {
            System.err.println("❌ Error al recuperar los pedidos enviados: " + e.getMessage());
        }
    }

    // ==========================================
    // LÓGICA DE NEGOCIO (Descuentos y Totales)
    // ==========================================

    private double calcularTotalPedido(Pedido pedido) {
        double total = 0.0;

        try {
            Articulo articulo = articuloDAO.obtenerPorId(pedido.getIdArticulo());
            Cliente cliente = clienteDAO.obtenerPorId(pedido.getIdCliente());

            if (articulo != null && cliente != null) {
                double costeArticulos = articulo.getPrecioVenta() * pedido.getCantidad();
                double gastosEnvio = articulo.getGastosEnvio();


                if (cliente instanceof ClientePremium) {
                    gastosEnvio = gastosEnvio * 0.80;
                }


                total = costeArticulos + gastosEnvio;
            }
        } catch (DAOException e) {
            System.err.println("❌ Error al obtener datos para calcular el total: " + e.getMessage());
        }

        return total;
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE CLIENTES
    // ==========================================

    public void anadirCliente(String email, String nombre, String domicilio, String nif, int tipoCliente)
            throws YaExisteException, DAOException, TipoClienteInvalidoException {

        // 1. ¡CAMBIO AQUÍ! Usamos el nuevo método específico para email
        if (clienteDAO.existePorEmail(email)) {
            throw new YaExisteException("cliente", email);
        }

        // 2. Creamos el objeto cliente dependiendo del tipo
        Cliente nuevoCliente;
        if (tipoCliente == 1) {
            nuevoCliente = new ClienteEstandar(email, nombre, domicilio, nif);
        } else if (tipoCliente == 2) {
            nuevoCliente = new ClientePremium(email, nombre, domicilio, nif);
        } else {
            throw new TipoClienteInvalidoException(tipoCliente);
        }

        // 3. Lo guardamos en la base de datos
        // Esto funciona porque el ID es AUTO_INCREMENT en MySQL y el objeto cliente tiene los datos
        clienteDAO.insertar(nuevoCliente);
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

        // 2. Buscamos al cliente para obtener su ID (o verificar existencia)
        Cliente cliente = clienteDAO.obtenerPorEmail(email);
        if (cliente == null) {
            throw new RecursoNoEncontradoException("cliente", email);
        }

        // 3. Borramos usando el email (o el ID si tu DAO está configurado así)
        // Suponiendo que tu DAO tiene un método borrarPorEmail o similar
        clienteDAO.eliminar(email);
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