package Factory;

import Excepciones.DAOException;
import DAO.Interfaces.PedidoDAO;
import DAO.Interfaces.ArticuloDAO;
import DAO.Interfaces.ClienteDAO;

public abstract class DAOFactory {

    /*Si usamos esta opción obliga al Coordinador a ser consciente de que Base de datos usamos, pierde un poco de consistencia el MVC
    Por otro lado es flexible si queremos poder cambiar entre bases de datos si tenemos la app abierta.
    public static final int MYSQL = 1;

        // --- MÉTODOS PARA OBTENER LOS DAOs ---

    public abstract PedidoDAO getPedidoDAO();


    public static DAOFactory getDAOFactory(int tipoBD) {
        switch (tipoBD) {
            case MYSQL:
                return new MySQLDAOFactory();
            default:
                return null;
        }

     */
    //Opción sin que Controlador tenga que saber que tipo de Base de datos usamos
    //Queremos una fábrica para toda la app
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
    public abstract PedidoDAO getPedidoDAO();
    public abstract ArticuloDAO getArticuloDAO() throws DAOException;
    public abstract ClienteDAO getClienteDAO() throws DAOException;
}