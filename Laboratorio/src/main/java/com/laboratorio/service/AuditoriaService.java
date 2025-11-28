package com.laboratorio.service;

import com.laboratorio.model.Auditoria;
import com.laboratorio.repository.AuditoriaRepository;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    @Autowired
    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAll();

    }

    public List<Auditoria> listarAuditorias() {
        return auditoriaRepository.findAllOrderByFechaHoraDesc();
    }

    public List<Auditoria> buscarAuditoria(String query) {
        return auditoriaRepository.buscarPorQuery(query);
    }

    public void registrarAuditoria(Auditoria auditoria) {
        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        if (inicio == null && fin == null) {
            return auditoriaRepository.findAllOrderByFechaHoraDesc();
        }

        if (inicio == null) {
            return auditoriaRepository.findByFechaHoraBeforeOrderByFechaHoraDesc(fin);
        }

        if (fin == null) {
            return auditoriaRepository.findByFechaHoraAfterOrderByFechaHoraDesc(inicio);
        }

        return auditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin);
    }

    public List<Auditoria> filtrar(String usuario, String modulo, String accion,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        List<Auditoria> auditorias;

        if (fechaInicio == null && fechaFin == null) {
            auditorias = auditoriaRepository.findAllOrderByFechaHoraDesc();
        } else if (fechaInicio == null) {
            auditorias = auditoriaRepository.findByFechaHoraBeforeOrderByFechaHoraDesc(fechaFin);
        } else if (fechaFin == null) {
            auditorias = auditoriaRepository.findByFechaHoraAfterOrderByFechaHoraDesc(fechaInicio);
        } else {
            auditorias = auditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
        }

        if (usuario != null && !usuario.isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getUsuario().toLowerCase().contains(usuario.toLowerCase()))
                    .toList();
        }

        if (modulo != null && !modulo.isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getModulo().toLowerCase().contains(modulo.toLowerCase()))
                    .toList();
        }

        if (accion != null && !accion.isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getAccion().toLowerCase().contains(accion.toLowerCase()))
                    .toList();
        }

        return auditorias;
    }

    public void exportarExcel(List<Auditoria> auditorias, OutputStream os) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reporte de Auditoria - Laboratorio");

        Row header = sheet.createRow(0);

        String[] columns = {"Fecha", "Módulo", "Usuario", "Descripción"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;

        for (Auditoria a : auditorias) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(a.getFechaHora().toString());
            row.createCell(1).setCellValue(a.getModulo());
            row.createCell(2).setCellValue(a.getUsuario());
            row.createCell(3).setCellValue(a.getDescripcion());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

}
