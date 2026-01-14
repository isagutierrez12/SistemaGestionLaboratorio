package com.laboratorio.exporter;

import com.laboratorio.model.Auditoria;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditoriaPDFExporter {

    private final List<Auditoria> listaAuditoria;

    private static final Color COLOR_PRINCIPAL = new Color(4, 187, 196);
    private static final Color COLOR_OSCURO = new Color(12, 108, 116);
    private static final Color COLOR_SECUNDARIO = new Color(28, 148, 164);
    private static final Color COLOR_BLANCO = Color.WHITE;

    public AuditoriaPDFExporter(List<Auditoria> listaAuditoria) {
        this.listaAuditoria = listaAuditoria;
    }

    private void agregarLogo(Document document) {
        try {
            com.lowagie.text.Image logo = com.lowagie.text.Image.getInstance(
                    getClass().getClassLoader()
                            .getResource("static/assets/img/logo-1.png")
            );
            logo.scaleToFit(120, 120);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
        }
    }

    private Paragraph fechaEmision() {
        Font fontFecha = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);

        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

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

        celda.setPhrase(new Phrase("Fecha", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Módulo", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Usuario", font));
        table.addCell(celda);

        celda.setPhrase(new Phrase("Descripción", font));
        table.addCell(celda);
    }

    private void escribirDatos(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 10);
        boolean alternarColor = false;

        for (Auditoria a : listaAuditoria) {
            Color fondo = alternarColor
                    ? new Color(230, 247, 248)
                    : Color.WHITE;

            table.addCell(celdaDato(a.getFechaHora().toString(), font, fondo));
            table.addCell(celdaDato(a.getModulo(), font, fondo));
            table.addCell(celdaDato(a.getUsuario(), font, fondo));
            table.addCell(celdaDato(a.getDescripcion(), font, fondo));

            alternarColor = !alternarColor;
        }
    }

    private PdfPCell celdaDato(String texto, Font font, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setBackgroundColor(fondo);
        c.setPadding(6);
        return c;
    }

    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Auditoria_General.pdf"
        );

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        agregarLogo(document);

        Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_SECUNDARIO);
        Paragraph titulo = new Paragraph(
                "Reporte de Auditoría\nLaboratorio Clínico Calderón Piedra",
                fontTitulo
        );
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10);
        document.add(titulo);

        document.add(fechaEmision());

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2f, 2f, 4f});
        table.setSpacingBefore(10);

        escribirCabecera(table);
        escribirDatos(table);

        document.add(table);
        document.close();
    }
}
