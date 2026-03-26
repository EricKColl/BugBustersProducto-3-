package Excepciones;

import java.sql.SQLException;

public class DAOException extends Exception {
    public DAOException(String mensaje, SQLException e) {
        super(mensaje);
    }
}