/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Paciente;
import com.laboratorio.model.Reporte;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.repository.SolicitudDetalleRepository;
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

    private final SolicitudDetalleRepository detalleRepository;

    public ReporteServiceImpl(SolicitudDetalleRepository detalleRepository) {
        this.detalleRepository = detalleRepository;
    }

    @Override
    public List<Reporte> generarReporte(
            LocalDate desde,
            LocalDate hasta,
            String area,
            String estado) {

        if (desde == null) {
            desde = LocalDate.now().minusMonths(1);
        }
        if (hasta == null) {
            hasta = LocalDate.now();
        }

        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(23, 59, 59);

        List<SolicitudDetalle> detalles = detalleRepository.findAllConRelaciones();

        List<Reporte> resultado = new ArrayList<>();

        for (SolicitudDetalle d : detalles) {

            LocalDateTime fechaSolicitud = d.getSolicitud().getFechaSolicitud();
            if (fechaSolicitud == null) {
                continue;
            }
            if (fechaSolicitud.isBefore(desdeDateTime) || fechaSolicitud.isAfter(hastaDateTime)) {
                continue;
            }

            String areaNombre = (d.getExamen() != null) ? d.getExamen().getArea() : null;

            if (area != null && !area.isBlank()) {
                if (areaNombre == null || !areaNombre.equalsIgnoreCase(area)) {
                    continue;
                }
            }

            String estadoReal = d.getSolicitud().getEstado();
            if (estado != null && !estado.isBlank()) {
                if (estadoReal == null || !estadoReal.equalsIgnoreCase(estado)) {
                    continue;
                }
            }

            String paciente = "";
            if (d.getSolicitud().getPaciente() != null) {
                Paciente p = d.getSolicitud().getPaciente();
                paciente = String.format("%s %s %s",
                        nullSafe(p.getNombre()),
                        nullSafe(p.getPrimerApellido()),
                        nullSafe(p.getSegundoApellido())
                ).trim();
            }

            String nombreExamen = (d.getExamen() != null) ? d.getExamen().getNombre() : "";

            double monto = d.getSolicitud().getPrecioTotal();

            Reporte dto = new Reporte();
            dto.setFecha(fechaSolicitud.toLocalDate());
            dto.setPaciente(paciente);
            dto.setExamen(nombreExamen);
            dto.setArea(areaNombre);
            dto.setEstado(estadoReal);
            dto.setMonto(monto);

            resultado.add(dto);
        }

        return resultado;
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void exportarExcel(List<Reporte> datos, OutputStream os) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte Exámenes");

        String[] columnas = {
            "Fecha", "Paciente", "Examen", "Área", "Estado", "Monto"
        };

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
