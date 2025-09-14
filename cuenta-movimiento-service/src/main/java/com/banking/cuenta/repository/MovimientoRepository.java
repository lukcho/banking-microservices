package com.banking.cuenta.repository;

import com.banking.cuenta.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    List<Movimiento> findByCuentaId(Long cuentaId);
    
    List<Movimiento> findByCuentaIdOrderByFechaDesc(Long cuentaId);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuentaId = :cuentaId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaIdAndFechaBetween(@Param("cuentaId") Long cuentaId, 
                                                   @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                   @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM Movimiento m JOIN Cuenta c ON m.cuentaId = c.cuentaId WHERE c.clienteId = :clienteId AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(@Param("clienteId") Long clienteId, 
                                                    @Param("fechaInicio") LocalDateTime fechaInicio, 
                                                    @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuentaId = :cuentaId ORDER BY m.fecha DESC LIMIT 1")
    Movimiento findLastMovimientoByCuentaId(@Param("cuentaId") Long cuentaId);
}
