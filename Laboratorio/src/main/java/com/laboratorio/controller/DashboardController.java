/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Dashboard;
import com.laboratorio.model.ExamenTop;
import com.laboratorio.model.InventarioAlerta;
import com.laboratorio.model.Reporte;
import com.laboratorio.service.DashboardService;
import com.laboratorio.service.InventarioAlertaService;
import com.laboratorio.service.ReporteService;
import java.time.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final InventarioAlertaService inventarioAlertaService;
    private final ReporteService reporteService;

    @Autowired
    public DashboardController(DashboardService dashboardService, InventarioAlertaService inventarioAlertaService, ReporteService reporteService) {
        this.dashboardService = dashboardService;
        this.inventarioAlertaService = inventarioAlertaService;
        this.reporteService = reporteService;
    }

    @GetMapping("/areas")
    public ResponseEntity<List<String>> obtenerAreas() {
        List<String> areas = dashboardService.obtenerAreas();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/resumen")
    public Dashboard obtenerResumen(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDate hoy = LocalDate.now();

        if (periodo != null) {
            switch (periodo.toUpperCase()) {
                case "DIA" -> {
                    desde = hoy;
                    hasta = hoy;
                }
                case "SEMANA" -> {
                    desde = hoy.with(DayOfWeek.MONDAY);
                    hasta = hoy;
                }
                case "MES" -> {
                    desde = hoy.withDayOfMonth(1);
                    hasta = hoy;
                }
            }
        }

        if (desde == null) {
            desde = hoy.withDayOfMonth(1);
        }
        if (hasta == null) {
            hasta = hoy;
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);

        return dashboardService.obtenerResumen(inicio, fin);
    }

    @GetMapping("/top-examenes")
    public List<ExamenTop> topExamenes(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            @RequestParam(required = false) String area,
            @RequestParam(defaultValue = "10") int limite
    ) {
        String areaValue = (area == null || area.isBlank()) ? null : area;
        return dashboardService.obtenerTopExamenes(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59),
                areaValue,
                limite
        );
    }

    @GetMapping("/inventario-alertas")
    public List<InventarioAlerta> obtenerAlertasInventario(
            @RequestParam(defaultValue = "TODAS") String tipo) {
        return inventarioAlertaService.obtenerAlertas(tipo);
    }

    @GetMapping("/reportes-examenes")
    public List<Reporte> obtenerReportes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String estado,
            @RequestParam(name = "examen", required = false) String nombreExamen
    ) {
        return reporteService.generarReporte(desde, hasta, area, estado, nombreExamen);
    }

    @GetMapping("/pagos")
    public List<com.laboratorio.model.PagoRow> obtenerPagosDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipoPago
    ) {
        return dashboardService.obtenerPagos(
                desde.atStartOfDay(),
                hasta.atTime(23, 59, 59),
                tipoPago
        );
    }

}
