package Factory;

import DAO.Interfaces.ArticuloDAO;
import DAO.MySQL.ArticuloDAOMySQL;
import Excepciones.DAOException;
import DAO.Interfaces.PedidoDAO;
import DAO.MySQL.PedidoDAOMySQL;
import Util.ConexionBD;
import DAO.Interfaces.ClienteDAO;
import DAO.MySQL.ClienteDAOMySQL;
import java.sql.Connection;
import java.sql.SQLException;

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

    @Override
    public Connection getConnection() throws DAOException {
        return ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void iniciarTransaccion() throws DAOException {
        try {
            ConexionBD.getInstancia().getConexion().setAutoCommit(false);
        } catch (SQLException e) {
            throw new DAOException("Error al iniciar transacción SQL", e);
        }
    }

    @Override
    public void confirmarTransaccion() throws DAOException {
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DAOException("Error al confirmar transacción SQL", e);
        }
    }

    @Override
    public void cancelarTransaccion() throws DAOException {
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DAOException("Error al cancelar transacción SQL", e);
        }
    }

}