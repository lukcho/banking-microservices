package com.banking.cuenta.controller;

import com.banking.cuenta.model.Movimiento;
import com.banking.cuenta.model.dto.EstadoCuentaDTO;
import com.banking.cuenta.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {
    
    @Autowired
    private MovimientoService movimientoService;
    
    @GetMapping
    public ResponseEntity<List<Movimiento>> getAllMovimientos() {
        try {
            List<Movimiento> movimientos = movimientoService.findAll();
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Movimiento> getMovimientoById(@PathVariable Long id) {
        try {
            Optional<Movimiento> movimiento = movimientoService.findById(id);
            return movimiento.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Movimiento>> getMovimientosByCuentaId(@PathVariable Long cuentaId) {
        try {
            List<Movimiento> movimientos = movimientoService.findByCuentaId(cuentaId);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cuenta/{cuentaId}/ordenados")
    public ResponseEntity<List<Movimiento>> getMovimientosByCuentaIdOrdered(@PathVariable Long cuentaId) {
        try {
            List<Movimiento> movimientos = movimientoService.findByCuentaIdOrderByFechaDesc(cuentaId);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createMovimiento(@Valid @RequestBody Movimiento movimiento) {
        try {
            Movimiento nuevoMovimiento = movimientoService.save(movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMovimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error interno del servidor");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimiento(@PathVariable Long id) {
        try {
            movimientoService.deleteById(id);
            return ResponseEntity.ok().body("Movimiento eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Error interno del servidor");
        }
    }
    
    @GetMapping("/reportes")
    public ResponseEntity<List<EstadoCuentaDTO>> generarEstadoCuenta(
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<EstadoCuentaDTO> estadosCuenta = movimientoService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
            return ResponseEntity.ok(estadosCuenta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(null);
        }
    }
}
