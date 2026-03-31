package Modelo;

import java.time.LocalDateTime;

public class Pedido {

    private int idPedido;
    private String idCliente;
    private String idArticulo;
    private int cantidad;
    private LocalDateTime fechaHora;
    private String estado;

    // 1. Constructor para CREAR un pedido NUEVO (Sin idPedido porque es AutoIncremental)
    public Pedido(String idCliente, String idArticulo, int cantidad, String estado) {
        this.idCliente = idCliente;
        this.idArticulo = idArticulo;
        this.cantidad = cantidad;
        this.estado = estado;
        this.fechaHora = LocalDateTime.now();
    }

    // 2. Constructor para LEER un pedido desde la Base de Datos (Con idPedido)
    public Pedido(int idPedido, String idCliente, String idArticulo, int cantidad, LocalDateTime fechaHora, String estado) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.idArticulo = idArticulo;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // GETTERS
    public int getIdPedido() { return idPedido; }
    public String getIdCliente() { return idCliente; }
    public String getIdArticulo() { return idArticulo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getEstado() { return estado; }

    @Override
    public String toString() {
        return "Pedido Nº: " + idPedido + " | ID Cliente: " + idCliente +
                " | ID Artículo: " + idArticulo + " | Cantidad: " + cantidad +
                " | Fecha: " + fechaHora + " | Estado: " + estado;
    }
}