package Modelo;

/**
 * Clase que representa un artículo de la tienda.
 *
 * Contiene la información básica necesaria para gestionar la venta y el envío de un artículo,
 * incluyendo su precio, gastos de envío, tiempo de preparación y cantidad disponible.
 *
 * @author BugBusters
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "Articulos")
public class Articulo {

    @Id
    private String codigo;
    private String descripcion;
    @Column(precision = 10, scale = 2)
    private double precioVenta;
    private double gastosEnvio;
    private int tiempoPreparacionMin; // minutos
    private int cantidadDisponible;

    public Articulo(String codigo, String descripcion, double precioVenta, double gastosEnvio, int tiempoPreparacionMin) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.gastosEnvio = gastosEnvio;
        this.tiempoPreparacionMin = tiempoPreparacionMin;
        this.cantidadDisponible = 0;
    }
    public Articulo() {
    }

    public Articulo(String codigo, String descripcion, double precioVenta, double gastosEnvio,
                    int tiempoPreparacionMin, int cantidadDisponible) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.gastosEnvio = gastosEnvio;
        this.tiempoPreparacionMin = tiempoPreparacionMin;
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getGastosEnvio() {
        return gastosEnvio;
    }

    public void setGastosEnvio(double gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }

    public int getTiempoPreparacionMin() {
        return tiempoPreparacionMin;
    }

    public void setTiempoPreparacionMin(int tiempoPreparacionMin) {
        this.tiempoPreparacionMin = tiempoPreparacionMin;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    @Override
    public String toString() {
        return "Artículo: " +
                "Código: " + codigo +
                " | Descripción: " + descripcion +
                " | Precio: " + String.format("%.2f €", precioVenta) +
                " | Gastos envío: " + String.format("%.2f €", gastosEnvio) +
                " | Tiempo preparación: " + tiempoPreparacionMin + " min" +
                " | Stock: " + cantidadDisponible;
    }
}