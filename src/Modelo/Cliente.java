package Modelo;

/**
 * Clase abstracta que representa un cliente genérico de la tienda.
 *
 * Define los atributos comunes a todos los clientes y los métodos abstractos
 * que deben implementar las clases hijas para calcular la cuota y el descuento de envío.
 *
 * @author BugBusters
 * @version 1.0
 * @since 1.0
 */

@Entity
@Table(name = "Clientes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cliente")
public abstract class Cliente {
    @Column(unique=true, nullable=false)
    private String email;
    private String nombre;
    private String domicilio;
    @Column(unique=true, nullable=false)
    private String nif;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCliente;

    public Cliente (String email, String nombre, String domicilio, String nif){
        this.email = email;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.nif = nif;
    }
    public Cliente(){}

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public int getIdCliente() {
        return idCliente;
    }
    public abstract double calcularCuota();
    public abstract double descuentoEnvio();

    // Devuelve true si el cliente es premium
    public boolean esPremium() {
        return this instanceof ClientePremium;
    }
}
