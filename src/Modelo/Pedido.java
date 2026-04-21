package Modelo;

import java.time.LocalDateTime;

@Entity
@Table(name = "Pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;
    @ManyToOne
    @JoinColumn(name ="id_cliente_fk", nullable = false)
    private Cliente cliente;
    @ManyToOne
    @JoinColumn(name="codigo_Articulo_fk", nullable = false)
    private Articulo articulo;
    private int cantidad;
    private LocalDateTime fechaHora;
    private String estado;

    public Pedido(Cliente cliente, Articulo articulo, int cantidad, LocalDateTime fechaHora, String estado) {
        this.cliente = cliente;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }
    public Pedido(){}

    public boolean puedeCancelar() {
        if (!"PENDIENTE".equalsIgnoreCase(this.estado)) return false;
        LocalDateTime limite = this.fechaHora.plusMinutes(this.articulo.getTiempoPreparacionMin());
        return LocalDateTime.now().isBefore(limite);
    }

    public boolean debeMarcarseComoEnviadoAutomaticamente() {
        return "PENDIENTE".equalsIgnoreCase(this.estado) && !puedeCancelar();
    }

    public double calcularTotal() {
        double descuento = cliente.descuentoEnvio();
        double precioTotalArticulos = articulo.getPrecioVenta() * cantidad;
        double gastosEnvioFinal = articulo.getGastosEnvio() * (1 - descuento);

        return precioTotalArticulos + gastosEnvioFinal;
    }

    public int getNumeroPedido() {
        return idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
