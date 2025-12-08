package com.laboratorio.exporter;

import com.laboratorio.model.ExamenTop;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TopExamenPDFExporter {

    private final List<ExamenTop> lista;

    public TopExamenPDFExporter(List<ExamenTop> lista) {
        this.lista = lista;
    }

    private void escribirCabecera(PdfPTable table) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(Color.LIGHT_GRAY);
        celda.setPadding(5);

        com.lowagie.text.Font font = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                12,
                com.lowagie.text.Font.BOLD
        );

        String[] headers = {"Examen", "Cantidad de solicitudes"};

        for (String h : headers) {
            celda.setPhrase(new Phrase(h, font));
            table.addCell(celda);
        }
    }

    private void escribirDatos(PdfPTable table) {
        com.lowagie.text.Font font = new com.lowagie.text.Font(
                com.lowagie.text.Font.HELVETICA,
                10
        );

        for (ExamenTop e : lista) {
            table.addCell(new Phrase(e.getNombreExamen(), font));
            table.addCell(new Phrase(String.valueOf(e.getCantidad()), font));
        }
    }

    public void exportPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=TopExamenes.pdf");

        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font fontTitulo = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    18,
                    com.lowagie.text.Font.BOLD
            );
            Paragraph titulo = new Paragraph("Top de exámenes solicitados", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(60f);
            table.setSpacingBefore(10);

            escribirCabecera(table);
            escribirDatos(table);

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("Error al generar PDF de Top de Exámenes", e);
        } finally {
            document.close();
        }
    }
}