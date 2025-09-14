package com.banking.cliente.service;

import com.banking.cliente.model.Cliente;
import com.banking.cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Optional<Cliente> findByIdentificacion(String identificacion) {
        return clienteRepository.findByIdentificacion(identificacion);
    }
    
    public List<Cliente> findByEstado(Boolean estado) {
        return clienteRepository.findByEstado(estado);
    }
    
    public List<Cliente> findByNombreContaining(String nombre) {
        return clienteRepository.findByNombreContaining(nombre);
    }
    
    public Cliente save(Cliente cliente) {
        if (clienteRepository.existsByIdentificacion(cliente.getIdentificacion())) {
            throw new RuntimeException("Ya existe un cliente con la identificación: " + cliente.getIdentificacion());
        }
        return clienteRepository.save(cliente);
    }
    
    public Cliente update(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
        
        // Verificar si la nueva identificación ya existe en otro cliente
        if (!clienteExistente.getIdentificacion().equals(clienteActualizado.getIdentificacion()) &&
            clienteRepository.existsByIdentificacion(clienteActualizado.getIdentificacion())) {
            throw new RuntimeException("Ya existe un cliente con la identificación: " + clienteActualizado.getIdentificacion());
        }
        
        // Actualizar campos
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setGenero(clienteActualizado.getGenero());
        clienteExistente.setEdad(clienteActualizado.getEdad());
        clienteExistente.setIdentificacion(clienteActualizado.getIdentificacion());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setContrasena(clienteActualizado.getContrasena());
        clienteExistente.setEstado(clienteActualizado.getEstado());
        
        return clienteRepository.save(clienteExistente);
    }
    
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return clienteRepository.existsById(id);
    }
    
    public boolean existsByIdentificacion(String identificacion) {
        return clienteRepository.existsByIdentificacion(identificacion);
    }
}
