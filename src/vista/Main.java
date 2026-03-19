package vista;

import Util.ConexionBD;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        try {
            Connection conexion = ConexionBD.obtenerConexion();

            if (conexion != null) {
                System.out.println("Conexion OK");
            }

            conexion.close();

        } catch (Exception e) {
            System.out.println("Error de conexion");
            e.printStackTrace();
        }
    }
}