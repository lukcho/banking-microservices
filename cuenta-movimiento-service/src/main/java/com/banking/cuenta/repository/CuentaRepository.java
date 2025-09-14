package com.banking.cuenta.repository;

import com.banking.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    List<Cuenta> findByClienteId(Long clienteId);
    
    List<Cuenta> findByEstado(Boolean estado);
    
    List<Cuenta> findByClienteIdAndEstado(Long clienteId, Boolean estado);
    
    boolean existsByNumeroCuenta(String numeroCuenta);
    
    @Query("SELECT c FROM Cuenta c WHERE c.clienteId = :clienteId AND c.estado = true")
    List<Cuenta> findActiveCuentasByClienteId(@Param("clienteId") Long clienteId);
}
