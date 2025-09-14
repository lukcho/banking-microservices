package com.banking.cuenta;

import com.banking.cuenta.model.Cuenta;
import com.banking.cuenta.model.Movimiento;
import com.banking.cuenta.repository.CuentaRepository;
import com.banking.cuenta.repository.MovimientoRepository;
import com.banking.cuenta.service.MovimientoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CuentaMovimientoIntegrationTest {
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private MovimientoService movimientoService;
    
    private Cuenta cuenta;
    
    @BeforeEach
    void setUp() {
        // Crear una cuenta de prueba
        cuenta = new Cuenta();
        cuenta.setNumeroCuenta("1234567890");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("1000.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);
        cuenta = cuentaRepository.save(cuenta);
    }
    
    @Test
    void testCrearMovimientoDeposito_Success() {
        // Given
        Movimiento deposito = new Movimiento();
        deposito.setTipoMovimiento("Deposito");
        deposito.setValor(new BigDecimal("500.00"));
        deposito.setCuentaId(cuenta.getCuentaId());
        
        // When
        Movimiento resultado = movimientoService.save(deposito);
        
        // Then
        assertNotNull(resultado);
        assertEquals("Deposito", resultado.getTipoMovimiento());
        assertEquals(new BigDecimal("500.00"), resultado.getValor());
        assertEquals(new BigDecimal("1500.00"), resultado.getSaldo()); // 1000 + 500
        assertEquals(cuenta.getCuentaId(), resultado.getCuentaId());
        
        // Verificar que se guardó en la base de datos
        List<Movimiento> movimientos = movimientoRepository.findByCuentaId(cuenta.getCuentaId());
        assertEquals(1, movimientos.size());
        assertEquals(resultado.getMovimientoId(), movimientos.get(0).getMovimientoId());
    }
    
    @Test
    void testCrearMovimientoRetiro_Success() {
        // Given
        Movimiento retiro = new Movimiento();
        retiro.setTipoMovimiento("Retiro");
        retiro.setValor(new BigDecimal("300.00"));
        retiro.setCuentaId(cuenta.getCuentaId());
        
        // When
        Movimiento resultado = movimientoService.save(retiro);
        
        // Then
        assertNotNull(resultado);
        assertEquals("Retiro", resultado.getTipoMovimiento());
        assertEquals(new BigDecimal("300.00"), resultado.getValor());
        assertEquals(new BigDecimal("700.00"), resultado.getSaldo()); // 1000 - 300
        assertEquals(cuenta.getCuentaId(), resultado.getCuentaId());
    }
    
    @Test
    void testCrearMovimientoRetiro_SaldoInsuficiente() {
        // Given
        Movimiento retiro = new Movimiento();
        retiro.setTipoMovimiento("Retiro");
        retiro.setValor(new BigDecimal("1500.00")); // Más que el saldo disponible
        retiro.setCuentaId(cuenta.getCuentaId());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movimientoService.save(retiro);
        });
        
        assertEquals("Saldo no disponible", exception.getMessage());
        
        // Verificar que no se creó ningún movimiento
        List<Movimiento> movimientos = movimientoRepository.findByCuentaId(cuenta.getCuentaId());
        assertEquals(0, movimientos.size());
    }
    
    @Test
    void testSecuenciaMovimientos() {
        // Given - Crear varios movimientos
        Movimiento deposito1 = new Movimiento();
        deposito1.setTipoMovimiento("Deposito");
        deposito1.setValor(new BigDecimal("200.00"));
        deposito1.setCuentaId(cuenta.getCuentaId());
        
        Movimiento retiro1 = new Movimiento();
        retiro1.setTipoMovimiento("Retiro");
        retiro1.setValor(new BigDecimal("150.00"));
        retiro1.setCuentaId(cuenta.getCuentaId());
        
        Movimiento deposito2 = new Movimiento();
        deposito2.setTipoMovimiento("Deposito");
        deposito2.setValor(new BigDecimal("100.00"));
        deposito2.setCuentaId(cuenta.getCuentaId());
        
        // When
        Movimiento resultado1 = movimientoService.save(deposito1);
        Movimiento resultado2 = movimientoService.save(retiro1);
        Movimiento resultado3 = movimientoService.save(deposito2);
        
        // Then
        assertEquals(new BigDecimal("1200.00"), resultado1.getSaldo()); // 1000 + 200
        assertEquals(new BigDecimal("1050.00"), resultado2.getSaldo()); // 1200 - 150
        assertEquals(new BigDecimal("1150.00"), resultado3.getSaldo()); // 1050 + 100
        
        // Verificar que todos los movimientos se guardaron
        List<Movimiento> movimientos = movimientoRepository.findByCuentaId(cuenta.getCuentaId());
        assertEquals(3, movimientos.size());
    }
    
    @Test
    void testGenerarEstadoCuenta() {
        // Given - Crear algunos movimientos
        Movimiento deposito = new Movimiento();
        deposito.setTipoMovimiento("Deposito");
        deposito.setValor(new BigDecimal("500.00"));
        deposito.setCuentaId(cuenta.getCuentaId());
        deposito.setFecha(LocalDateTime.now().minusDays(1));
        movimientoRepository.save(deposito);
        
        Movimiento retiro = new Movimiento();
        retiro.setTipoMovimiento("Retiro");
        retiro.setValor(new BigDecimal("200.00"));
        retiro.setCuentaId(cuenta.getCuentaId());
        retiro.setFecha(LocalDateTime.now());
        movimientoRepository.save(retiro);
        
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
        
        // When
        List<com.banking.cuenta.model.dto.EstadoCuentaDTO> estados = 
            movimientoService.generarEstadoCuenta(1L, fechaInicio, fechaFin);
        
        // Then
        assertNotNull(estados);
        assertEquals(1, estados.size());
        
        com.banking.cuenta.model.dto.EstadoCuentaDTO estado = estados.get(0);
        assertEquals(cuenta.getNumeroCuenta(), estado.getNumeroCuenta());
        assertEquals(cuenta.getTipoCuenta(), estado.getTipo());
        assertEquals(cuenta.getSaldoInicial(), estado.getSaldoInicial());
        assertEquals(new BigDecimal("300.00"), estado.getMovimiento()); // 500 - 200
        assertEquals(new BigDecimal("1300.00"), estado.getSaldoDisponible()); // 1000 + 500 - 200
        assertEquals(2, estado.getMovimientos().size());
    }
}
