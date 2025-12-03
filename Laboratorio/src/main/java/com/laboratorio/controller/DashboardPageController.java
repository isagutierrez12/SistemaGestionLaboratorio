/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Reporte;
import com.laboratorio.exporter.ReportePDFExporter;
import com.laboratorio.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardPageController {

    private final ReporteService reporteService;

    // Inyección por constructor
    public DashboardPageController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String mostrarDashboard() {
        return "dashboard/dashboard";
    }

    //PDF
    @GetMapping("/reportes/examenes/pdf")
    public void exportarReportePdf(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws IOException {

        LocalDate fDesde = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde)
                : LocalDate.now().minusMonths(1);

        LocalDate fHasta = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta)
                : LocalDate.now();

        List<Reporte> datos = reporteService.generarReporte(
                fDesde, fHasta, area, estado
        );

        ReportePDFExporter exporter = new ReportePDFExporter(datos);
        exporter.exportPdf(response);
    }

    //Excel
    @GetMapping("/reportes/examenes/excel")
    public void exportarReporteExcel(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String estado,
            HttpServletResponse response) throws IOException {

        LocalDate fDesde = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde)
                : LocalDate.now().minusMonths(1);

        LocalDate fHasta = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta)
                : LocalDate.now();

        List<Reporte> datos = reporteService.generarReporte(
                fDesde, fHasta, area, estado
        );

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ReporteExamenes.xlsx");

        reporteService.exportarExcel(datos, response.getOutputStream());
    }
}
