package com.laboratorio.exporter;

import com.laboratorio.model.Reporte;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ReportePDFExporter {

    private final List<Reporte> lista;

    public ReportePDFExporter(List<Reporte> lista) {
        this.lista = lista;
    }

    //PDF
    private void escribirCabeceraPdf(PdfPTable table) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.LIGHT_GRAY);
        celda.setPadding(5);

        com.lowagie.text.Font font = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                12,
                com.lowagie.text.Font.BOLD
        );

        String[] headers = {"Fecha", "Paciente", "Examen", "Área", "Estado", "Monto"};

        for (String h : headers) {
            celda.setPhrase(new Phrase(h, font));
            table.addCell(celda);
        }
    }

    private void escribirDatosPdf(PdfPTable table) {
        com.lowagie.text.Font font = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                10
        );

        for (Reporte r : lista) {
            table.addCell(new Phrase(r.getFecha() != null ? r.getFecha().toString() : "", font));
            table.addCell(new Phrase(r.getPaciente(), font));
            table.addCell(new Phrase(r.getExamen(), font));
            table.addCell(new Phrase(r.getArea(), font));
            table.addCell(new Phrase(r.getEstado(), font));
            table.addCell(new Phrase(String.valueOf(r.getMonto()), font));
        }
    }

    public void exportPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ReporteExamenes.pdf");

        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font fontTitulo = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    18,
                    com.lowagie.text.Font.BOLD
            );
            Paragraph titulo = new Paragraph("Reporte de exámenes", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);

            escribirCabeceraPdf(table);
            escribirDatosPdf(table);

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("Error al generar PDF", e);
        } finally {
            document.close();
        }
    }
}