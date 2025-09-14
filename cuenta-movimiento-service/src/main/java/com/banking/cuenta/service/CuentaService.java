package com.banking.cuenta.service;

import com.banking.cuenta.model.Cuenta;
import com.banking.cuenta.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CuentaService {
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }
    
    public Optional<Cuenta> findById(Long id) {
        return cuentaRepository.findById(id);
    }
    
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta);
    }
    
    public List<Cuenta> findByClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }
    
    public List<Cuenta> findByEstado(Boolean estado) {
        return cuentaRepository.findByEstado(estado);
    }
    
    public List<Cuenta> findActiveCuentasByClienteId(Long clienteId) {
        return cuentaRepository.findActiveCuentasByClienteId(clienteId);
    }
    
    public Cuenta save(Cuenta cuenta) {
        if (cuentaRepository.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + cuenta.getNumeroCuenta());
        }
        return cuentaRepository.save(cuenta);
    }
    
    public Cuenta update(Long id, Cuenta cuentaActualizada) {
        Cuenta cuentaExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + id));
        
        // Verificar si el nuevo número de cuenta ya existe en otra cuenta
        if (!cuentaExistente.getNumeroCuenta().equals(cuentaActualizada.getNumeroCuenta()) &&
            cuentaRepository.existsByNumeroCuenta(cuentaActualizada.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + cuentaActualizada.getNumeroCuenta());
        }
        
        // Actualizar campos
        cuentaExistente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
        cuentaExistente.setTipoCuenta(cuentaActualizada.getTipoCuenta());
        cuentaExistente.setSaldoInicial(cuentaActualizada.getSaldoInicial());
        cuentaExistente.setEstado(cuentaActualizada.getEstado());
        cuentaExistente.setClienteId(cuentaActualizada.getClienteId());
        
        return cuentaRepository.save(cuentaExistente);
    }
    
    public void deleteById(Long id) {
        if (!cuentaRepository.existsById(id)) {
            throw new RuntimeException("Cuenta no encontrada con ID: " + id);
        }
        cuentaRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return cuentaRepository.existsById(id);
    }
    
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.existsByNumeroCuenta(numeroCuenta);
    }
}
