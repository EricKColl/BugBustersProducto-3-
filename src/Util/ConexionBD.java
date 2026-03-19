package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://autorack.proxy.rlwy.net:13802/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "SppuTCrhvoNHXhezDpJcwTINkOenYool";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}