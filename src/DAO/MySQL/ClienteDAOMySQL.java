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

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        // 1. Extraemos los datos de las columnas de la tabla 'clientes'
        int id = rs.getInt("id_cliente");
        String email = rs.getString("email");
        String nombre = rs.getString("nombre");
        String domicilio = rs.getString("domicilio");
        String nif = rs.getString("nif");
        String tipo = rs.getString("tipo_cliente"); // 'estandar' o 'premium'

        // 2. Dependiendo de lo que diga la columna 'tipo_cliente', creamos un hijo u otro
        if ("premium".equalsIgnoreCase(tipo)) {
            return new ClientePremium(email, nombre, domicilio, nif);
        } else {
            return new ClienteEstandar(email, nombre, domicilio, nif);
        }
    }

    @Override
    public List<Cliente> obtenerClientesEstandar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE tipo_cliente = 'estandar'";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            // Manejo de error básico, lo ideal sería lanzar DAOException si la firma lo permitiera
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cliente> obtenerClientesPremium() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE tipo_cliente = 'premium'";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void insertar(Cliente cliente) throws DAOException {
        // Usamos el Procedimiento Almacenado que creaste en SQL
        String sql = "{CALL insertar_cliente(?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, cliente.getEmail());
            cs.setString(2, cliente.getNombre());
            cs.setString(3, cliente.getDomicilio());
            cs.setString(4, cliente.getNif());

            // Determinamos el tipo de cliente según la clase hija
            String tipo = (cliente instanceof ClientePremium) ? "premium" : "estandar";
            cs.setString(5, tipo);

            cs.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al insertar el cliente con email: " + cliente.getEmail(), e);
        }
    }

    @Override
    public List<Cliente> obtenerTodos() throws DAOException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener todos los clientes", e);
        }
        return lista;
    }

    @Override
    public Cliente obtenerPorId(Integer id) throws DAOException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id); // Usamos 'id'
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al obtener cliente por ID: " + id, e);
        }
        return null;
    }

    @Override
    public Cliente buscar(Integer idCliente) {
        try {
            return obtenerPorId(idCliente);
        } catch (DAOException e) {
            return null;
        }
    }

    @Override
    public boolean existe(Integer idCliente) {
        String sql = "SELECT 1 FROM clientes WHERE id_cliente = ?"; // Cambiado a id_cliente
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente); // Cambiado a setInt
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Si hay un resultado, existe
            }
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void eliminar(Integer idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?"; // Cambiado a id_cliente
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCliente); // Cambiado a setInt
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
    public Cliente obtenerPorEmail(String email) throws DAOException {
        String sql = "SELECT * FROM clientes WHERE email = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar por email", e);
        }
        return null;
    }
}
