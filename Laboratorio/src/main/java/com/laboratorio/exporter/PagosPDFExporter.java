package com.laboratorio.exporter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.laboratorio.model.PagoRow;

public class PagosPDFExporter {

    private final List<PagoRow> listaPagos;

    private static final Color COLOR_PRINCIPAL = new Color(4, 187, 196);
    private static final Color COLOR_OSCURO = new Color(12, 108, 116);
    private static final Color COLOR_SECUNDARIO = new Color(28, 148, 164);
    private static final Color COLOR_BLANCO = Color.WHITE;

    public PagosPDFExporter(List<PagoRow> listaPagos) {
        this.listaPagos = listaPagos;
    }

    private void agregarLogo(Document document) {
        try {
            Image logo = Image.getInstance(
                    getClass().getClassLoader().getResource("static/assets/img/logo-1.png")
            );
            logo.scaleToFit(120, 120);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception ignored) {}
    }

    private Paragraph fechaEmision() {
        Font fontFecha = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        Paragraph p = new Paragraph("Fecha de emisión: " + fecha, fontFecha);
        p.setAlignment(Element.ALIGN_RIGHT);
        p.setSpacingAfter(10);
        return p;
    }

    private void escribirCabecera(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 11, Font.BOLD, COLOR_BLANCO);

        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(COLOR_OSCURO);
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);

        celda.setPhrase(new Phrase("Fecha y hora", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Paciente", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Monto (CRC)", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Método de pago", font));
        table.addCell(celda);
    }

    private PdfPCell celdaDato(String texto, Font font, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(fondo);
        c.setPadding(6);
        return c;
    }

    private void escribirDatos(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 10);
        boolean alternar = false;

        for (PagoRow p : listaPagos) {
            Color fondo = alternar ? new Color(230, 247, 248) : Color.WHITE;

            table.addCell(celdaDato(String.valueOf(p.getFechaCita()), font, fondo));
            table.addCell(celdaDato(nvl(p.getPaciente()), font, fondo));
            table.addCell(celdaDato(String.format("%.2f", p.getMonto()), font, fondo));
            table.addCell(celdaDato(nvl(p.getTipoPago()), font, fondo));

            alternar = !alternar;
        }
    }

    private String nvl(String s) { return s == null ? "" : s; }

    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Pagos_Registrados.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        agregarLogo(document);

        Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_SECUNDARIO);
        Paragraph titulo = new Paragraph("Reporte de Pagos\nLaboratorio Clínico Calderón Piedra", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        document.add(fechaEmision());

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.2f, 3.2f, 1.6f, 2.0f});
        table.setSpacingBefore(10);

        escribirCabecera(table);
        escribirDatos(table);

        document.add(table);
        document.close();
    }
}