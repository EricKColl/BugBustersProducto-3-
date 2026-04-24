package DAO.MySQL;

import DAO.Interfaces.ArticuloDAO;
import Excepciones.DAOException;
import Modelo.Articulo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ArticuloDAOJPA implements ArticuloDAO {

    // Sustituimos Connection por EntityManager
    private final EntityManager em;

    public ArticuloDAOJPA(EntityManager em) {
        this.em = em;
    }

    @Override
    public void insertar(Articulo articulo) throws DAOException {
        // Adiós al CallableStatement y al procedimiento almacenado
        try {
            em.persist(articulo);
        } catch (Exception e) {
            throw new DAOException("Error al insertar artículo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Articulo> obtenerTodos() throws DAOException {
        // Usamos JPQL para consultar a la Entidad Articulo, no a la tabla
        try {
            String jpql = "SELECT a FROM Articulo a";
            TypedQuery<Articulo> query = em.createQuery(jpql, Articulo.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DAOException("Error de JPA al obtener la lista de artículos: " + e.getMessage(), e);
        }
    }

    @Override
    public Articulo obtenerPorId(String codigo) throws DAOException {
        // JPA busca automáticamente por la clave primaria (@Id)
        try {
            return em.find(Articulo.class, codigo);
        } catch (Exception e) {
            throw new DAOException("Error de JPA al buscar el artículo con código '" + codigo + "': " + e.getMessage(), e);
        }
    }

    @Override
    public void sumarStock(String codigo, int cantidad) throws DAOException {
        // En JPA simplemente obtenemos el objeto, modificamos su estado y lo actualizamos (merge)
        try {
            Articulo articulo = em.find(Articulo.class, codigo);
            if (articulo != null) {
                articulo.setCantidadDisponible(articulo.getCantidadDisponible() + cantidad);
                em.merge(articulo);
            }
        } catch (Exception e) {
            throw new DAOException("Error al sumar stock del artículo: " + e.getMessage(), e);
        }
    }

    private boolean tienePedidosAsociados(String codigoArticulo) {
        String jpql = "SELECT COUNT(p) FROM Pedido p WHERE p.articulo.codigo = :codigo";

        try {
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("codigo", codigoArticulo)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void eliminar(String codigo) throws DAOException {
        if (tienePedidosAsociados(codigo)) {
            throw new DAOException("No se puede eliminar el artículo porque tiene pedidos asociados.");
        }

        // Buscamos la entidad y si existe, la eliminamos del contexto
        try {
            Articulo articulo = em.find(Articulo.class, codigo);
            if (articulo == null) {
                throw new DAOException("El artículo con código '" + codigo + "' no existe.");
            }

            em.remove(articulo);
        } catch (Exception e) {
            throw new DAOException("Error al ejecutar eliminar artículo: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Articulo articulo) throws DAOException {
        try {
            em.merge(articulo);
        } catch (Exception e) {
            throw new DAOException("Error al actualizar el artículo: " + e.getMessage(), e);
        }
    }
}