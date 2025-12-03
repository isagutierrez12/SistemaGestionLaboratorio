/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Reporte;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<Reporte> generarReporte(
            LocalDate desde,
            LocalDate hasta,
            String area,
            String estado
    );
    
    void exportarExcel(List<Reporte> datos, OutputStream os) throws IOException;
}
