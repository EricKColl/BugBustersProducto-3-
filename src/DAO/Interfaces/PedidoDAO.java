package DAO.Interfaces;

import Modelo.Pedido;
import Excepciones.DAOException;
import java.util.List;

public interface PedidoDAO {

    void insertar(Pedido pedido) throws DAOException;

    Pedido obtenerPorId(int id) throws DAOException;

    List<Pedido> obtenerTodos() throws DAOException;

    List<Pedido> obtenerPorCliente(int idCliente) throws DAOException;

    void actualizar(Pedido pedido) throws DAOException;

    void eliminar(int id) throws DAOException;

    List<Pedido> obtenerPedidosPendientes(int idCliente) throws DAOException;

    List<Pedido> obtenerPedidosEnviados(int idCliente) throws DAOException;
}