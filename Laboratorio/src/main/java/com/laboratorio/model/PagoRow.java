/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PagoRow {
    private LocalDateTime fechaCita;
    private String paciente;
    private Double monto;
    private String tipoPago;

    public PagoRow(LocalDateTime fechaCita, String paciente, Double monto, String tipoPago) {
        this.fechaCita = fechaCita;
        this.paciente = paciente;
        this.monto = monto;
        this.tipoPago = tipoPago;
    }

    public LocalDateTime getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDateTime fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    
}