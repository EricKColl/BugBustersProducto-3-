package Factory;

import Excepciones.DAOException;
import DAO.Interfaces.PedidoDAO;
import DAO.Interfaces.ArticuloDAO;
import DAO.Interfaces.ClienteDAO;
import java.sql.Connection;

public abstract class DAOFactory {

    private static DAOFactory instancia;
    public static final int MYSQL = 1;

    //Método para obtener la fábrica
    public static DAOFactory getDAOFactory(int tipo) {
        if (instancia == null) {
            //Indicamos que base de datos usamos
            instancia = new MySQLDAOFactory();
        }
        return instancia;
    }
    // --- MÉTODOS PARA OBTENER LOS DAOs ---
    public abstract PedidoDAO getPedidoDAO() throws DAOException;
    public abstract ArticuloDAO getArticuloDAO() throws DAOException;
    public abstract ClienteDAO getClienteDAO() throws DAOException;
    public abstract Connection getConnection() throws DAOException;

    // Gestión de transacciones
    public abstract void iniciarTransaccion() throws DAOException;
    public abstract void confirmarTransaccion() throws DAOException;
    public abstract void cancelarTransaccion() throws DAOException;
}