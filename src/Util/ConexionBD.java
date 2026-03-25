package Util;

import Excepciones.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://autorack.proxy.rlwy.net:13802/producto3";
    private static final String USER = "root";
    private static final String PASSWORD = "SppuTCrhvoNHXhezDpJcwTINkOenYool";

    //Hacemos que se abra una única vez la conexión (mejora de eficiencia)
    private static ConexionBD instancia;

    // Objeto que mantiene la sesión abierta
    private Connection conexion;

    private ConexionBD(){
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Método para abrir la conexión
    public void conectar() throws DAOException {
        try {
            if (conexion == null || conexion.isClosed()) {
                // DriverManager es una clase de Java que busca el driver de MySQL y abre la puerta
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new DAOException("Error grave: No se pudo conectar a la base de datos MySQL.", e);
        }
    }

    // Método para cerrar la conexión
    public void desconectar() throws DAOException {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al intentar cerrar la conexión con la base de datos.", e);
        }
    }
    public Connection getConexion() {
        return conexion;
    }
}