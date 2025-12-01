package com.laboratorio.exporter;

import com.laboratorio.model.AuditoriaCriticos;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import com.lowagie.text.Font;

public class AuditoriaCriticosPDFExporter {

    private final List<AuditoriaCriticos> listaCriticos;

    public AuditoriaCriticosPDFExporter(List<AuditoriaCriticos> listaCriticos) {
        this.listaCriticos = listaCriticos;
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

        celda.setPhrase(new Phrase("Descripción", font));
        table.addCell(celda);
    }

    private void escribirDatos(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 10);

        for (AuditoriaCriticos a : listaCriticos) {
            table.addCell(new Phrase(a.getFechaHora().toString(), font));
            table.addCell(new Phrase(a.getUsuario(), font));
            table.addCell(new Phrase(a.getDescripcion(), font));
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");

        response.setHeader("Content-Disposition", "attachment; filename=Auditoria_Criticos.pdf");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Auditorías Críticas: Laboratorio Clínico Calderón Piedra", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);

            document.add(titulo);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);

            escribirCabecera(table);
            escribirDatos(table);

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("Error al generar el PDF de auditorías críticas", e);
        } finally {
            document.close();
        }
    }
}