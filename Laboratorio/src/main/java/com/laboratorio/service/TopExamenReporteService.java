/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.ExamenTop;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

public interface TopExamenReporteService {

    List<ExamenTop> generarTop(
            LocalDate desde,
            LocalDate hasta,
            String area,
            int limite
    );

    void exportarExcel(List<ExamenTop> datos, OutputStream os) throws IOException;
}
