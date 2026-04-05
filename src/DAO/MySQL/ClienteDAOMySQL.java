package DAO.MySQL;

import DAO.Interfaces.ClienteDAO;
import Excepciones.DAOException;
import Modelo.Cliente;
import Modelo.ClienteEstandar;
import Modelo.ClientePremium;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOMySQL implements ClienteDAO {

    private final Connection conexion;

    public ClienteDAOMySQL(Connection conexion) {
        this.conexion = conexion;
    }

    private Cliente crearSegunTipo(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_cliente");
        String email = rs.getString("email");
        String nombre = rs.getString("nombre");
        String domicilio = rs.getString("domicilio");
        String nif = rs.getString("nif");
        String tipo = rs.getString("tipo_cliente");

        Cliente cliente;

        if ("premium".equalsIgnoreCase(tipo)) {
            cliente = new ClientePremium(email, nombre, domicilio, nif);
        } else {
            cliente = new ClienteEstandar(email, nombre, domicilio, nif);
        }

        cliente.setIdCliente(id);
        return cliente;
    }

    @Override
    public List<Cliente> obtenerClientesEstandar() throws DAOException{
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE tipo_cliente = 'estandar'";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearSegunTipo(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener clientes estándar", e);
        }

        return lista;
    }

    @Override
    public List<Cliente> obtenerClientesPremium() throws DAOException{
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE tipo_cliente = 'premium'";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearSegunTipo(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener clientes premium", e);
        }

        return lista;
    }

    @Override
    public void insertar(Cliente cliente) throws DAOException {
        String sql = "{call insertar_cliente(?, ?, ?, ?, ?)}";
        String tipo = (cliente instanceof ClientePremium) ? "premium" : "estandar";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, cliente.getEmail());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getDomicilio());
            cs.setString(4, cliente.getNif());
            cs.setString(5, tipo);

            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Error al insertar cliente mediante procedimiento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> obtenerTodos() throws DAOException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearSegunTipo(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener la lista de clientes", e);
        }
        return lista;
    }

    @Override
    public Cliente obtenerPorId(Integer id) throws DAOException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearSegunTipo(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener cliente por ID: " + id, e);
        }

        return null;
    }

    private boolean tienePedidosAsociados(Integer idCliente) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE id_cliente = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            return true;
        }
        return false;
    }

    @Override
    public void eliminar(Integer idCliente) throws DAOException {
        // Lógica de negocio: Validamos antes de llamar al procedimiento
        if (tienePedidosAsociados(idCliente)) {
            throw new DAOException("No se puede eliminar el cliente porque tiene pedidos asociados.");
        }

        String sql = "{CALL eliminar_cliente(?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idCliente);
            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar cliente (ID: " + idCliente + ") en la base de datos.", e);
        }
    }

    @Override
    public boolean existePorEmail(String email) throws DAOException {
        String sql = "SELECT 1 FROM clientes WHERE email = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al comprobar email", e);
        }
    }

    @Override
    public Cliente buscarPorEmail(String email) throws DAOException {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearSegunTipo(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar cliente por email", e);
        }
        return null;
    }
}