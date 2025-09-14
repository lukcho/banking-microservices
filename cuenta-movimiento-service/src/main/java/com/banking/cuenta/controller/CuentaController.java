package com.banking.cuenta.controller;

import com.banking.cuenta.model.Cuenta;
import com.banking.cuenta.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {
    
    @Autowired
    private CuentaService cuentaService;
    
    @GetMapping
    public ResponseEntity<List<Cuenta>> getAllCuentas() {
        try {
            List<Cuenta> cuentas = cuentaService.findAll();
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> getCuentaById(@PathVariable Long id) {
        try {
            Optional<Cuenta> cuenta = cuentaService.findById(id);
            return cuenta.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/numero/{numeroCuenta}")
    public ResponseEntity<Cuenta> getCuentaByNumero(@PathVariable String numeroCuenta) {
        try {
            Optional<Cuenta> cuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
            return cuenta.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cuenta>> getCuentasByClienteId(@PathVariable Long clienteId) {
        try {
            List<Cuenta> cuentas = cuentaService.findByClienteId(clienteId);
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Cuenta>> getCuentasByEstado(@PathVariable Boolean estado) {
        try {
            List<Cuenta> cuentas = cuentaService.findByEstado(estado);
            return ResponseEntity.ok(cuentas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createCuenta(@Valid @RequestBody Cuenta cuenta) {
        try {
            Cuenta nuevaCuenta = cuentaService.save(cuenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error interno del servidor");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCuenta(@PathVariable Long id, @Valid @RequestBody Cuenta cuenta) {
        try {
            Cuenta cuentaActualizada = cuentaService.update(id, cuenta);
            return ResponseEntity.ok(cuentaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error interno del servidor");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCuenta(@PathVariable Long id) {
        try {
            cuentaService.deleteById(id);
            return ResponseEntity.ok().body("Cuenta eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error interno del servidor");
        }
    }
}
