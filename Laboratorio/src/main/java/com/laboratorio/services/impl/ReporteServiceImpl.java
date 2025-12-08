/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Cita;
import com.laboratorio.model.Paciente;
import com.laboratorio.model.Reporte;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.repository.CitaRepository;
import com.laboratorio.service.ReporteService;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final CitaRepository citaRepository;

    public ReporteServiceImpl(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    @Override
    public List<Reporte> generarReporte(
            LocalDate desde,
            LocalDate hasta,
            String area,
            String estado,
            String nombreExamen) {

        if (desde == null) {
            desde = LocalDate.now().minusMonths(1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }

        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(23, 59, 59);

        List<Cita> citas = citaRepository.findCitasConDetallesEnRango(desdeDateTime, hastaDateTime);

        String areaFiltro = (area != null && !area.isBlank()) ? area.trim().toLowerCase() : null;
        String estadoFiltro = (estado != null && !estado.isBlank()) ? estado.trim().toLowerCase() : null;
        String examenFiltro = (nombreExamen != null && !nombreExamen.isBlank())
                ? nombreExamen.trim().toLowerCase()
                : null;

        List<Reporte> resultado = new ArrayList<>();

        for (Cita c : citas) {

            Solicitud sol = c.getSolicitud();
            if (sol == null || sol.getDetalles() == null) {
                continue;
            }

            String estadoReal = c.getEstado(); // puede ser null

            if (estadoFiltro != null) {
                if (estadoReal == null || !estadoReal.trim().toLowerCase().equals(estadoFiltro)) {
                    continue;
                }
            } else {
                if (estadoReal != null && estadoReal.trim().equalsIgnoreCase("CANCELADA")) {
                    continue;
                }
            }

            String nombrePaciente = "";
            if (sol.getPaciente() != null) {
                Paciente p = sol.getPaciente();
                StringBuilder sb = new StringBuilder();
                if (p.getNombre() != null) sb.append(p.getNombre());
                if (p.getPrimerApellido() != null) sb.append(" ").append(p.getPrimerApellido());
                if (p.getSegundoApellido() != null) sb.append(" ").append(p.getSegundoApellido());
                nombrePaciente = sb.toString().trim();
            }

            for (SolicitudDetalle d : sol.getDetalles()) {

                String areaExamen = null;
                String nombreExamenActual = "";
                if (d.getExamen() != null) {
                    areaExamen = d.getExamen().getArea();
                    nombreExamenActual = d.getExamen().getNombre();
                } else if (d.getPaquete() != null) {
                    nombreExamenActual = d.getPaquete().getNombre();
                }

                if (areaFiltro != null) {
                    if (areaExamen == null
                            || !areaExamen.trim().toLowerCase().equals(areaFiltro)) {
                        continue;
                    }
                }

                if (examenFiltro != null) {
                    if (nombreExamenActual == null
                            || !nombreExamenActual.toLowerCase().contains(examenFiltro)) {
                        continue;
                    }
                }

                double monto = sol.getPrecioTotal();

                Reporte dto = new Reporte();
                if (c.getFechaCita() != null) {
                    dto.setFecha(c.getFechaCita().toLocalDate());
                }
                dto.setPaciente(nombrePaciente);
                dto.setExamen(nombreExamenActual);
                dto.setArea(areaExamen);
                dto.setEstado(estadoReal);
                dto.setMonto(monto);

                resultado.add(dto);
            }
        }

        return resultado;
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void exportarExcel(List<Reporte> datos, OutputStream os) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("ReporteExamenes");

        String[] columnas = {"Fecha", "Paciente", "Examen", "Área", "Estado", "Monto"};

        Row header = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        int idx = 1;

        for (Reporte r : datos) {
            Row row = sheet.createRow(idx++);

            row.createCell(0).setCellValue(r.getFecha() != null ? r.getFecha().toString() : "");
            row.createCell(1).setCellValue(r.getPaciente() != null ? r.getPaciente() : "");
            row.createCell(2).setCellValue(r.getExamen() != null ? r.getExamen() : "");
            row.createCell(3).setCellValue(r.getArea() != null ? r.getArea() : "");
            row.createCell(4).setCellValue(r.getEstado() != null ? r.getEstado() : "");
            row.createCell(5).setCellValue(r.getMonto());
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }
}