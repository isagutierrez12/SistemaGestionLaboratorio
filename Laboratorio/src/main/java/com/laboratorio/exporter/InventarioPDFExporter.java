package com.laboratorio.exporter;

import com.laboratorio.model.Inventario;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import com.lowagie.text.Font;


public class InventarioPDFExporter {

    private final List<Inventario> lista;

    public InventarioPDFExporter(List<Inventario> lista) {
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

        String[] headers = {
                "Código Barras", "Insumo", "Tipo",
                "Stock Actual", "Stock Mínimo", "Fecha Vencimiento"
        };

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

        for (Inventario inv : lista) {

            table.addCell(new Phrase(inv.getCodigoBarras(), font));
            table.addCell(new Phrase(inv.getInsumo().getNombre(), font));
            table.addCell(new Phrase(inv.getInsumo().getTipo(), font));
            table.addCell(new Phrase(String.valueOf(inv.getStockActual()), font));
            table.addCell(new Phrase(String.valueOf(inv.getStockMinimo()), font));
            table.addCell(new Phrase(
                    inv.getFechaVencimiento() != null
                            ? inv.getFechaVencimiento().toString()
                            : "",
                    font
            ));
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Inventario.pdf");

        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font fontTitulo = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA,
                    18,
                    com.lowagie.text.Font.BOLD
            );

            Paragraph titulo = new Paragraph("Reporte de Inventario", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);

            escribirCabecera(table);
            escribirDatos(table);

            document.add(table);

        } catch (DocumentException e) {
            throw new IOException("Error al generar PDF", e);
        } finally {
            document.close();
        }
    }
}
