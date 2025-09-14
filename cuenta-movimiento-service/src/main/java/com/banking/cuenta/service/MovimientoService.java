package com.banking.cuenta.service;

import com.banking.cuenta.model.Cuenta;
import com.banking.cuenta.model.Movimiento;
import com.banking.cuenta.model.dto.EstadoCuentaDTO;
import com.banking.cuenta.model.dto.MovimientoDTO;
import com.banking.cuenta.repository.CuentaRepository;
import com.banking.cuenta.repository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovimientoService {
    
    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    public List<Movimiento> findAll() {
        return movimientoRepository.findAll();
    }
    
    public Optional<Movimiento> findById(Long id) {
        return movimientoRepository.findById(id);
    }
    
    public List<Movimiento> findByCuentaId(Long cuentaId) {
        return movimientoRepository.findByCuentaId(cuentaId);
    }
    
    public List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId) {
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId);
    }
    
    public List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin);
    }
    
    public List<Movimiento> findByClienteIdAndFechaBetween(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
    }
    
    public Movimiento save(Movimiento movimiento) {
        // Validar que la cuenta existe
        Cuenta cuenta = cuentaRepository.findById(movimiento.getCuentaId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + movimiento.getCuentaId()));
        
        if (!cuenta.getEstado()) {
            throw new RuntimeException("La cuenta está inactiva");
        }
        
        // Calcular el saldo actual
        BigDecimal saldoActual = calcularSaldoActual(movimiento.getCuentaId());
        BigDecimal nuevoSaldo;
        
        if ("Deposito".equals(movimiento.getTipoMovimiento())) {
            nuevoSaldo = saldoActual.add(movimiento.getValor());
        } else if ("Retiro".equals(movimiento.getTipoMovimiento())) {
            if (saldoActual.compareTo(movimiento.getValor()) < 0) {
                throw new RuntimeException("Saldo no disponible");
            }
            nuevoSaldo = saldoActual.subtract(movimiento.getValor());
        } else {
            throw new RuntimeException("Tipo de movimiento no válido");
        }
        
        movimiento.setSaldo(nuevoSaldo);
        return movimientoRepository.save(movimiento);
    }
    
    public void deleteById(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new RuntimeException("Movimiento no encontrado con ID: " + id);
        }
        movimientoRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return movimientoRepository.existsById(id);
    }
    
    private BigDecimal calcularSaldoActual(Long cuentaId) {
        Movimiento ultimoMovimiento = movimientoRepository.findLastMovimientoByCuentaId(cuentaId);
        if (ultimoMovimiento != null) {
            return ultimoMovimiento.getSaldo();
        } else {
            // Si no hay movimientos, usar el saldo inicial de la cuenta
            Cuenta cuenta = cuentaRepository.findById(cuentaId)
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
            return cuenta.getSaldoInicial();
        }
    }
    
    public List<EstadoCuentaDTO> generarEstadoCuenta(Long clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Cuenta> cuentas = cuentaRepository.findActiveCuentasByClienteId(clienteId);
        List<EstadoCuentaDTO> estadosCuenta = new ArrayList<>();
        
        for (Cuenta cuenta : cuentas) {
            List<Movimiento> movimientos = findByCuentaIdAndFechaBetween(cuenta.getCuentaId(), fechaInicio, fechaFin);
            
            // Calcular total de movimientos en el período
            BigDecimal totalMovimientos = movimientos.stream()
                    .map(mov -> "Deposito".equals(mov.getTipoMovimiento()) ? mov.getValor() : mov.getValor().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Obtener saldo actual
            BigDecimal saldoActual = calcularSaldoActual(cuenta.getCuentaId());
            
            // Convertir movimientos a DTOs
            List<MovimientoDTO> movimientosDTO = movimientos.stream()
                    .map(mov -> new MovimientoDTO(mov.getFecha(), mov.getTipoMovimiento(), mov.getValor(), mov.getSaldo()))
                    .collect(Collectors.toList());
            
            EstadoCuentaDTO estadoCuenta = new EstadoCuentaDTO(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    "Cliente " + clienteId, // En un caso real, se obtendría del servicio de clientes
                    cuenta.getNumeroCuenta(),
                    cuenta.getTipoCuenta(),
                    cuenta.getSaldoInicial(),
                    cuenta.getEstado(),
                    totalMovimientos,
                    saldoActual,
                    movimientosDTO
            );
            
            estadosCuenta.add(estadoCuenta);
        }
        
        return estadosCuenta;
    }
}
