package com.banking.cliente;

import com.banking.cliente.model.Cliente;
import com.banking.cliente.repository.ClienteRepository;
import com.banking.cliente.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @InjectMocks
    private ClienteService clienteService;
    
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setGenero("M");
        cliente.setEdad(30);
        cliente.setIdentificacion("12345678");
        cliente.setDireccion("Calle 123 #45-67");
        cliente.setTelefono("3001234567");
        cliente.setContrasena("password123");
        cliente.setEstado(true);
    }
    
    @Test
    void testSaveCliente_Success() {
        // Given
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // When
        Cliente result = clienteService.save(cliente);
        
        // Then
        assertNotNull(result);
        assertEquals(cliente.getNombre(), result.getNombre());
        assertEquals(cliente.getIdentificacion(), result.getIdentificacion());
        verify(clienteRepository, times(1)).existsByIdentificacion(cliente.getIdentificacion());
        verify(clienteRepository, times(1)).save(cliente);
    }
    
    @Test
    void testSaveCliente_DuplicateIdentificacion() {
        // Given
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.save(cliente);
        });
        
        assertEquals("Ya existe un cliente con la identificación: " + cliente.getIdentificacion(), 
                    exception.getMessage());
        verify(clienteRepository, times(1)).existsByIdentificacion(cliente.getIdentificacion());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void testFindById_Success() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        
        // When
        Optional<Cliente> result = clienteService.findById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(cliente.getClienteId(), result.get().getClienteId());
        verify(clienteRepository, times(1)).findById(1L);
    }
    
    @Test
    void testFindById_NotFound() {
        // Given
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<Cliente> result = clienteService.findById(999L);
        
        // Then
        assertFalse(result.isPresent());
        verify(clienteRepository, times(1)).findById(999L);
    }
    
    @Test
    void testUpdateCliente_Success() {
        // Given
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setNombre("Juan Carlos Pérez");
        clienteActualizado.setIdentificacion("12345678");
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        
        // When
        Cliente result = clienteService.update(1L, clienteActualizado);
        
        // Then
        assertNotNull(result);
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
    
    @Test
    void testUpdateCliente_NotFound() {
        // Given
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.update(999L, cliente);
        });
        
        assertEquals("Cliente no encontrado con ID: 999", exception.getMessage());
        verify(clienteRepository, times(1)).findById(999L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
    
    @Test
    void testDeleteById_Success() {
        // Given
        when(clienteRepository.existsById(1L)).thenReturn(true);
        
        // When
        clienteService.deleteById(1L);
        
        // Then
        verify(clienteRepository, times(1)).existsById(1L);
        verify(clienteRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteById_NotFound() {
        // Given
        when(clienteRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.deleteById(999L);
        });
        
        assertEquals("Cliente no encontrado con ID: 999", exception.getMessage());
        verify(clienteRepository, times(1)).existsById(999L);
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
