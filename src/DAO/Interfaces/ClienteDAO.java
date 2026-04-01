package DAO.Interfaces;

import Modelo.Cliente;
import Excepciones.DAOException;
import java.util.List;

public interface ClienteDAO extends GenericoDAO<Cliente, Integer> {

    List<Cliente> obtenerClientesEstandar();

    List<Cliente> obtenerClientesPremium();

    boolean existePorEmail(String email) throws DAOException;
    Cliente obtenerPorEmail(String email) throws DAOException;
}