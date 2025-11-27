/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.exporter;

import com.laboratorio.model.Auditoria;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import com.lowagie.text.Font;

public class AuditoriaPDFExporter {

    private final List<Auditoria> listaAuditoria;

    public AuditoriaPDFExporter(List<Auditoria> listaAuditoria) {
        this.listaAuditoria = listaAuditoria;
    }

    private void escribirCabecera(PdfPTable table) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.LIGHT_GRAY);
        celda.setPadding(6);

        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);

        celda.setPhrase(new Phrase("Fecha", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Usuario", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Módulo", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Acción", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Descripción", font));
        table.addCell(celda);
    }

    private void escribirDatos(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 10);

        for (Auditoria a : listaAuditoria) {
            table.addCell(new Phrase(a.getFechaHora().toString(), font));
            table.addCell(new Phrase(a.getUsuario(), font));
            table.addCell(new Phrase(a.getModulo(), font));
            table.addCell(new Phrase(a.getAccion(), font));
            table.addCell(new Phrase(a.getDescripcion(), font));
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");

        // Nombre del archivo
        response.setHeader("Content-Disposition", "attachment; filename=auditoria_general.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Reporte de Auditoría: Laboratorio Clínico Calderón Piedra", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);

        document.add(titulo);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        escribirCabecera(table);
        escribirDatos(table);

        document.add(table);
        document.close();
    }
}