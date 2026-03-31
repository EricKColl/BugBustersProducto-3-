package DAO.Interfaces;

import Modelo.Articulo;
import Modelo.Pedido;
import Excepciones.DAOException;
import java.util.List;

public interface PedidoDAO extends GenericoDAO<Pedido, Integer>{

    void actualizar(Pedido pedido) throws DAOException;

    List<Pedido> obtenerPedidosPendientes(int idCliente) throws DAOException;

    List<Pedido> obtenerPedidosEnviados(int idCliente) throws DAOException;
}