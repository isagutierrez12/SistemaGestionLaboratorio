/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import java.math.BigDecimal;

public class Dashboard {

    private BigDecimal ingresosTotales;
    private Long pacientesAtendidos;
    private Double promedioExamenesPorPaciente;

    public Dashboard() {
    }

    public Dashboard(BigDecimal ingresosTotales,
                               Long pacientesAtendidos,
                               Double promedioExamenesPorPaciente) {
        this.ingresosTotales = ingresosTotales;
        this.pacientesAtendidos = pacientesAtendidos;
        this.promedioExamenesPorPaciente = promedioExamenesPorPaciente;
    }

    public BigDecimal getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(BigDecimal ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public Long getPacientesAtendidos() {
        return pacientesAtendidos;
    }

    public void setPacientesAtendidos(Long pacientesAtendidos) {
        this.pacientesAtendidos = pacientesAtendidos;
    }

    public Double getPromedioExamenesPorPaciente() {
        return promedioExamenesPorPaciente;
    }

    public void setPromedioExamenesPorPaciente(Double promedioExamenesPorPaciente) {
        this.promedioExamenesPorPaciente = promedioExamenesPorPaciente;
    }
}