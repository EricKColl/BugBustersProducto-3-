package Modelo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Clase que representa un cliente premium de la tienda.
 *
 * Los clientes premium pagan una cuota fija de 30€ y tienen un 20% de descuento
 * en los gastos de envío. Estos valores son fijos y no modificables por el usuario.
 *
 * @author BugBusters
 * @version 1.0
 * @since 1.0
 */
@Entity
@DiscriminatorValue("Premium")
public class ClientePremium extends Cliente {

    public ClientePremium(String email, String nombre, String domicilio, String nif) {
        super(email, nombre, domicilio, nif);
    }

    public ClientePremium() {
    }

    @Override
    public double calcularCuota() {
        return 30;
    }

    @Override
    public double descuentoEnvio() {
        return 0.2;
    }

    @Override
    public String toString() {
        return "[Cliente Premium] " +
                "Email: " + getEmail() +
                " | Nombre: " + getNombre() +
                " | Domicilio: " + getDomicilio() +
                " | NIF: " + getNif() +
                " | Cuota: " + String.format("%.2f €", calcularCuota()) +
                " | Descuento: " + (descuentoEnvio() * 100) + "%";
    }
}