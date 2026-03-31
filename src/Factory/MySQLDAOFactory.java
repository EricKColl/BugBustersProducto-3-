package Factory;

import DAO.Interfaces.ArticuloDAO;
import DAO.MySQL.ArticuloDAOMySQL;
import Excepciones.DAOException;
import DAO.Interfaces.PedidoDAO;
import DAO.MySQL.PedidoDAOMySQL;
import Util.ConexionBD;
import DAO.Interfaces.ClienteDAO;
import DAO.MySQL.ClienteDAOMySQL;

public class MySQLDAOFactory extends DAOFactory {

    @Override
    public ArticuloDAO getArticuloDAO() throws DAOException {
        // Pedimos la instancia única de ConexionBD
        ConexionBD con = ConexionBD.getInstancia();

        //Nos aseguramos que esté la base de datos conectada.
        con.conectar();

        // Construimos el DAO de MySQL y le inyectamos la conexión abierta
        return new ArticuloDAOMySQL(con.getConexion());
    }

    @Override
    public PedidoDAO getPedidoDAO() throws DAOException{
        ConexionBD con = ConexionBD.getInstancia();
        con.conectar();
        return new PedidoDAOMySQL(con.getConexion());
    }

    @Override
    public ClienteDAO getClienteDAO() throws DAOException {
        ConexionBD con = ConexionBD.getInstancia();
        con.conectar();
        return new ClienteDAOMySQL(con.getConexion());
    }

}