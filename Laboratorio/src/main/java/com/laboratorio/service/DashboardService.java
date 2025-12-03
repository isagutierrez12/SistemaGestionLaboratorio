/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Dashboard;
import com.laboratorio.model.ExamenTop;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardService {

    Dashboard obtenerResumen(LocalDateTime inicio, LocalDateTime fin);

    List<ExamenTop> obtenerTopExamenes(LocalDateTime inicio,
                                       LocalDateTime fin,
                                       String area,
                                       int limite);
    
    List<String> obtenerAreas();
}
