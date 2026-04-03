package Vista;

import Controlador.Controlador;
import Excepciones.DAOException;
import Modelo.Articulo;
import Modelo.Cliente;
import Modelo.Pedido;
import Modelo.Excepciones.*;

import java.util.List;
import java.util.Scanner;

public class Vista {

    private final Scanner teclado;
    private final Controlador controlador;

    public Vista() {
        teclado = new Scanner(System.in);
        controlador = new Controlador();
    }

    public void iniciar() {
        int opcion;

        TerminalUI.showWelcome();

        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Selecciona una opción: ");

            switch (opcion) {
                case 1:
                    menuArticulos();
                    break;
                case 2:
                    menuClientes();
                    break;
                case 3:
                    menuPedidos();
                    break;
                case 0:
                    TerminalUI.info("Saliendo del programa...");
                    TerminalUI.showGoodbye();
                    break;
                default:
                    TerminalUI.error("Opción no válida.");
            }

        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        TerminalUI.showMenu("MENÚ PRINCIPAL", new String[]{
                "1. Gestión de artículos",
                "2. Gestión de clientes",
                "3. Gestión de pedidos",
                "0. Salir"
        });
    }

    private void menuArticulos() {
        int opcion;

        do {
            TerminalUI.showMenu("GESTIÓN DE ARTÍCULOS", new String[]{
                    "1. Añadir artículo",
                    "2. Mostrar artículos",
                    "3. Eliminar artículo",
                    "0. Volver"
            });

            opcion = leerEntero("Selecciona una opción: ");

            switch (opcion) {
                case 1:
                    anadirArticulo();
                    break;
                case 2:
                    mostrarArticulos();
                    break;
                case 3:
                    eliminarArticulo();
                    break;
                case 0:
                    break;
                default:
                    TerminalUI.error("Opción no válida.");
            }

        } while (opcion != 0);
    }

    private void anadirArticulo() {
        TerminalUI.sectionTitle("AÑADIR ARTÍCULO");

        try {
            String codigo = leerTextoNoVacio("Código: ");

            try {
                controlador.buscarArticulo(codigo);

                TerminalUI.error("El artículo con código '" + codigo + "' ya existe.");
                return;

            } catch (RecursoNoEncontradoException e) {
                // No hacemos nada: que no exista es lo que buscamos para poder insertarlo.
            }
            String descripcion = leerTextoNoVacio("Descripción: ");
            double precioVenta = leerDouble("Precio de venta: ");
            double gastosEnvio = leerDouble("Gastos de envío: ");
            int tiempoPreparacionMin = leerEntero("Tiempo de preparación (minutos): ");

            controlador.anadirArticulo(codigo, descripcion, precioVenta, gastosEnvio, tiempoPreparacionMin);
            TerminalUI.success("¡Artículo añadido correctamente!");

        } catch (DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
        TerminalUI.sciFiDivider();
    }

    private void mostrarArticulos() {
        TerminalUI.sectionTitle("LISTADO DE ARTÍCULOS");

        try {
            TerminalUI.showArticlesTable(controlador.obtenerTodosArticulos());
        } catch (DAOException e) {
            TerminalUI.error("Error al conectar con la base de datos para obtener el listado: " + e.getMessage());
        }
    }

    private void eliminarArticulo() {
        TerminalUI.sectionTitle("ELIMINAR ARTÍCULO");

        String codigo = leerTextoNoVacio("Introduce el código del artículo: ");

        try {
            Articulo articulo = controlador.buscarArticulo(codigo);

            TerminalUI.info("Artículo localizado correctamente.");
            TerminalUI.showArticleCard(articulo);

            String conf = leerTextoNoVacio("¿Estás seguro de eliminar este artículo? (S/N): ");
            if (!conf.equalsIgnoreCase("S")) {
                TerminalUI.warning("Operación cancelada.");
                return;
            }

            controlador.eliminarArticulo(codigo);

            TerminalUI.success("¡Artículo eliminado!");
            TerminalUI.spotlight("OPERACIÓN FINALIZADA");

        } catch (RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void menuClientes() {
        int opcion;

        do {
            TerminalUI.showMenu("GESTIÓN DE CLIENTES", new String[]{
                    "1. Añadir cliente",
                    "2. Buscar cliente",
                    "3. Mostrar todos los clientes",
                    "4. Mostrar clientes estándar",
                    "5. Mostrar clientes premium",
                    "6. Eliminar cliente",
                    "0. Volver"
            });

            opcion = leerEntero("Selecciona una opción: ");

            switch (opcion) {
                case 1:
                    anadirCliente();
                    break;
                case 2:
                    buscarCliente();
                    break;
                case 3:
                    obtenerTodosClientes();
                    break;
                case 4:
                    obtenerClientesEstandar();
                    break;
                case 5:
                    obtenerClientesPremium();
                    break;
                case 6:
                    eliminarCliente();
                    break;
                case 0:
                    break;
                default:
                    TerminalUI.error("Opción no válida.");
            }

        } while (opcion != 0);
    }

    private void anadirCliente() {
        TerminalUI.sectionTitle("AÑADIR CLIENTE");

        try {
            String email = leerTextoNoVacio("Email: ");
            controlador.emailValido(email);
            controlador.existeCliente(email);

            String nombre = leerTextoNoVacio("Nombre: ");
            String domicilio = leerTextoNoVacio("Domicilio: ");
            String nif = leerTextoNoVacio("NIF: ");

            int tipoCliente = leerEntero("Tipo de cliente (1- Estándar, 2- Premium): ");

            controlador.anadirCliente(email, nombre, domicilio, nif, tipoCliente);
            TerminalUI.success("¡Cliente añadido correctamente!");

        } catch (DAOException | EmailInvalidoException | TipoClienteInvalidoException e) {
            TerminalUI.exception(e.getMessage());
        }

        TerminalUI.sciFiDivider();
    }

    private void buscarCliente() {
        TerminalUI.sectionTitle("BUSCAR CLIENTE");

        String email = leerTextoNoVacio("Introduce el Email del cliente: ");

        try {
            Cliente clienteEncontrado = controlador.buscarCliente(email);
            TerminalUI.showClientCard(clienteEncontrado);

        } catch (EmailInvalidoException | RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void obtenerTodosClientes() {
        TerminalUI.sectionTitle("LISTADO DE TODOS LOS CLIENTES");

        try {
            List<Cliente> lista = controlador.obtenerTodosClientes();
            imprimirClientes("No hay clientes registrados.", lista);

        } catch (DAOException e) {
            TerminalUI.exception("Error al acceder a los datos: " + e.getMessage());
        }

        TerminalUI.sciFiDivider();
    }

    private void obtenerClientesEstandar() {
        TerminalUI.sectionTitle("LISTADO DE CLIENTES ESTÁNDAR");
        try {
            List<Cliente> lista = controlador.obtenerClientesEstandar();
            imprimirClientes("No hay clientes estándar registrados.", lista);
        } catch (DAOException e) {
            TerminalUI.error("Error al recuperar los clientes: " + e.getMessage());
        }
    }

    private void obtenerClientesPremium() {
        TerminalUI.sectionTitle("LISTADO DE CLIENTES PREMIUM");
        try {
            imprimirClientes("No hay clientes premium registrados.", controlador.obtenerClientesPremium());
        } catch (DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void eliminarCliente() {
        TerminalUI.sectionTitle("ELIMINAR CLIENTE");
        String email = leerTextoNoVacio("Introduce el Email del cliente a eliminar: ");

        try {
            // 1. Buscamos el objeto
            Cliente aEliminar = controlador.buscarCliente(email);

            TerminalUI.info("Cliente localizado correctamente.");
            TerminalUI.showClientCard(aEliminar);

            String conf = leerTextoNoVacio("¿Estás seguro de eliminar a este cliente? (S/N): ");
            if (!conf.equalsIgnoreCase("S")) return;

            // 2. Pasamos el OBJETO al controlador, no el email
            controlador.eliminarCliente(aEliminar);

            TerminalUI.success("¡Cliente eliminado con éxito!");

        } catch (EmailInvalidoException | RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void menuPedidos() {
        int opcion;

        do {
            TerminalUI.showMenu("GESTIÓN DE PEDIDOS", new String[]{
                    "1. Añadir pedido",
                    "2. Eliminar pedido",
                    "3. Mostrar pedidos pendientes",
                    "4. Mostrar pedidos enviados",
                    "5. Cambiar estado de pedido",
                    "0. Volver"
            });

            opcion = leerEntero("Selecciona una opción: ");

            switch (opcion) {
                case 1:
                    anadirPedido();
                    break;
                case 2:
                    eliminarPedido();
                    break;
                case 3:
                    mostrarPedidosPendientes();
                    break;
                case 4:
                    mostrarPedidosEnviados();
                    break;
                case 5:
                    cambiarEstadoPedido();
                    break;
                case 0:
                    break;
                default:
                    TerminalUI.error("Opción no válida.");
            }

        } while (opcion != 0);
    }

    private void anadirPedido() {
        TerminalUI.sectionTitle("AÑADIR PEDIDO");
        String emailCliente = leerTextoNoVacio("Email del cliente: ");

        try {
            controlador.emailValido(emailCliente);
        } catch (EmailInvalidoException e) {
            TerminalUI.exception(e.getMessage());
            return;
        }

        Cliente cliente = null;

        try {
            cliente = controlador.buscarCliente(emailCliente);
            TerminalUI.info("Cliente encontrado.");
            TerminalUI.showClientCard(cliente);

        } catch (EmailInvalidoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
            return;

        } catch (RecursoNoEncontradoException e) {
            TerminalUI.warning("El cliente no existe. ¿Desea crearlo? (s/n): ");
            String respuesta = leerTextoNoVacio("");

            if (respuesta.equalsIgnoreCase("s")) {
                TerminalUI.info("Procedemos a la creación del cliente.");
                String nombre = leerTextoNoVacio("Nombre: ");
                String domicilio = leerTextoNoVacio("Domicilio: ");
                String nif = leerTextoNoVacio("NIF: ");
                int tipoSeleccionado = leerEntero("Tipo cliente (1-Estándar, 2-Premium): ");

                try {
                    cliente = controlador.anadirCliente(emailCliente, nombre, domicilio, nif, tipoSeleccionado);
                    TerminalUI.success("¡Cliente creado correctamente!");
                } catch (TipoClienteInvalidoException | DAOException | EmailInvalidoException ex) {
                    TerminalUI.exception(ex.getMessage());
                    return;
                }
            } else {
                TerminalUI.error("Operación cancelada.");
                return;
            }
        }

        TerminalUI.info("Procedemos a la creación del pedido.");
        String codigoArticulo = leerTextoNoVacio("Código del artículo: ");

        Articulo articulo = null;
        try {
            articulo = controlador.buscarArticulo(codigoArticulo);
            TerminalUI.showArticleCard(articulo);
        } catch (RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
            return;
        }

        int cantidad = leerEntero("Cantidad: ");
        int tiempoTotal = articulo.getTiempoPreparacionMin() * cantidad;

        try {
            Pedido pedido = controlador.anadirPedido(emailCliente, codigoArticulo, cantidad);
            TerminalUI.success("Pedido creado correctamente para " + cliente.getNombre() + ".");
            TerminalUI.info("Tiempo estimado: " + tiempoTotal + " minutos");
            TerminalUI.showOrderCard(pedido);
            TerminalUI.spotlight("OPERACIÓN COMPLETADA CON ÉXITO");

        } catch (EmailInvalidoException | RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void eliminarPedido() {
        TerminalUI.sectionTitle("ELIMINAR PEDIDO");

        int numeroPedido = leerEntero("Número de pedido: ");

        try {
            controlador.eliminarPedido(numeroPedido);
            TerminalUI.success("¡Pedido eliminado correctamente!");
            TerminalUI.spotlight("PEDIDO CANCELADO");
        } catch (RecursoNoEncontradoException | PedidoNoCancelableException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void mostrarPedidosPendientes() {
        TerminalUI.sectionTitle("PEDIDOS PENDIENTES");
        String emailFiltro = leerTextoOpcional("Filtrar por email del cliente (dejar vacío para todos): ");

        try {
            List<Pedido> pedidos = controlador.obtenerPedidosPendientes(emailFiltro);

            if (pedidos.isEmpty()) {
                TerminalUI.empty("No hay pedidos pendientes que mostrar.");
            } else {
                TerminalUI.showOrdersTable(pedidos);
            }
        } catch (EmailInvalidoException | RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void mostrarPedidosEnviados() {
        TerminalUI.sectionTitle("PEDIDOS ENVIADOS");
        String emailFiltro = leerTextoOpcional("Filtrar por email del cliente (dejar vacío para todos): ");
        try {
            List<Pedido> pedidos = controlador.obtenerPedidosEnviados(emailFiltro);

            if (pedidos.isEmpty()) {
                TerminalUI.empty("No hay pedidos enviados que mostrar.");
            } else {
                TerminalUI.showOrdersTable(pedidos);
            }

        } catch (EmailInvalidoException | RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private void cambiarEstadoPedido() {
        TerminalUI.sectionTitle("CAMBIAR ESTADO DE PEDIDO");

        int numeroPedido = leerEntero("Número de pedido: ");
        int opcionEstado = leerEntero("Nuevo estado (1-PENDIENTE, 2-ENVIADO): ");

        String nuevoEstado;
        switch (opcionEstado) {
            case 1:
                nuevoEstado = "PENDIENTE";
                break;
            case 2:
                nuevoEstado = "ENVIADO";
                break;
            default:
                TerminalUI.error("Opción de estado no válida.");
                return;
        }

        try {
            controlador.cambiarEstadoPedido(numeroPedido, nuevoEstado);
            TerminalUI.success("Estado del pedido actualizado correctamente a " + nuevoEstado + ".");
            TerminalUI.spotlight("ESTADO DEL PEDIDO ACTUALIZADO");
        } catch (RecursoNoEncontradoException | DAOException e) {
            TerminalUI.exception(e.getMessage());
        }
    }

    private String leerTextoNoVacio(String mensaje) {
        while (true) {
            TerminalUI.prompt(mensaje);
            String linea = teclado.nextLine().trim();

            if (!linea.isEmpty()) {
                return linea;
            }

            TerminalUI.error("El texto no puede estar vacío. Inténtalo de nuevo.");
        }
    }

    private String leerTextoOpcional(String mensaje) {
        TerminalUI.prompt(mensaje);
        return teclado.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            TerminalUI.prompt(mensaje);
            String linea = teclado.nextLine().trim();

            if (linea.isEmpty()) {
                TerminalUI.error("No se permiten valores vacíos.");
                continue;
            }

            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                TerminalUI.error("Debes introducir un número válido.\n");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            TerminalUI.prompt(mensaje);
            String linea = teclado.nextLine().trim();

            if (linea.isEmpty()) {
                TerminalUI.error("No se permiten valores vacíos.");
                continue;
            }

            try {
                return Double.parseDouble(linea.replace(',', '.'));
            } catch (NumberFormatException e) {
                TerminalUI.error("Debes introducir un número válido.\n");
            }
        }
    }

    private void imprimirClientes(String mensajePersonalizado, List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            TerminalUI.empty(mensajePersonalizado);
        } else {
            TerminalUI.showClientsTable(clientes);
        }
    }
}