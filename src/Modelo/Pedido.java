package Modelo;

import java.time.LocalDateTime;

public class Pedido {
    private int idPedido;
    private Cliente cliente;   // OBJETO, no String
    private Articulo articulo; // OBJETO, no String
    private int cantidad;
    private LocalDateTime fechaHora;
    private String estado;

    public Pedido(int idPedido, Cliente cliente, Articulo articulo, int cantidad, LocalDateTime fechaHora, String estado) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public boolean puedeCancelar() {
        return "PENDIENTE".equalsIgnoreCase(this.estado);
    }

    public double calcularTotal() {
        double descuento = cliente.descuentoEnvio();
        double precioTotalArticulos = articulo.getPrecioVenta() * cantidad;
        double gastosEnvioFinal = articulo.getGastosEnvio() * (1 - descuento);

        return precioTotalArticulos + gastosEnvioFinal;
    }

    public int getNumeroPedido() { return idPedido; }
    public Cliente getCliente() { return cliente; }
    public Articulo getArticulo() { return articulo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}