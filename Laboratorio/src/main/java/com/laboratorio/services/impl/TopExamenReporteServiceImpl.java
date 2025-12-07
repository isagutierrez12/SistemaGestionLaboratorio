/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.ExamenTop;
import com.laboratorio.service.DashboardService;
import com.laboratorio.service.TopExamenReporteService;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class TopExamenReporteServiceImpl implements TopExamenReporteService {

    private final DashboardService dashboardService;

    public TopExamenReporteServiceImpl(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private LocalDateTime inicioDeDia(LocalDate d) {
        return d.atStartOfDay();
    }

    private LocalDateTime finDeDia(LocalDate d) {
        return d.atTime(23, 59, 59);
    }

    @Override
    public List<ExamenTop> generarTop(
            LocalDate desde,
            LocalDate hasta,
            String area,
            int limite
    ) {
        if (desde == null) {
            desde = LocalDate.now().minusMonths(1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }

        LocalDateTime inicio = inicioDeDia(desde);
        LocalDateTime fin = finDeDia(hasta);

        String areaParam = (area == null || area.isBlank()) ? null : area;

        return dashboardService.obtenerTopExamenes(inicio, fin, areaParam, limite);
    }

    @Override
    public void exportarExcel(List<ExamenTop> datos, OutputStream os) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("TopExamenes");

        String[] columnas = {"Examen", "Cantidad de solicitudes"};

        Row header = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        int rowIdx = 1;
        for (ExamenTop e : datos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getNombreExamen());
            row.createCell(1).setCellValue(e.getCantidad());
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }
}