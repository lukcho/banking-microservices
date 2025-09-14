package com.banking.cuenta.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EstadoCuentaDTO {
    
    private String fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private BigDecimal movimiento;
    private BigDecimal saldoDisponible;
    private List<MovimientoDTO> movimientos;
    
    // Constructores
    public EstadoCuentaDTO() {}
    
    public EstadoCuentaDTO(String fecha, String cliente, String numeroCuenta, String tipo, 
                          BigDecimal saldoInicial, Boolean estado, BigDecimal movimiento, 
                          BigDecimal saldoDisponible, List<MovimientoDTO> movimientos) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.estado = estado;
        this.movimiento = movimiento;
        this.saldoDisponible = saldoDisponible;
        this.movimientos = movimientos;
    }
    
    // Getters y Setters
    public String getFecha() {
        return fecha;
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public String getCliente() {
        return cliente;
    }
    
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }
    
    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public BigDecimal getMovimiento() {
        return movimiento;
    }
    
    public void setMovimiento(BigDecimal movimiento) {
        this.movimiento = movimiento;
    }
    
    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }
    
    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }
    
    public List<MovimientoDTO> getMovimientos() {
        return movimientos;
    }
    
    public void setMovimientos(List<MovimientoDTO> movimientos) {
        this.movimientos = movimientos;
    }
}
