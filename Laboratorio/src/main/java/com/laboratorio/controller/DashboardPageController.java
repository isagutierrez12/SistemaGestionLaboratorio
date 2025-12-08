/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Reporte;
import com.laboratorio.exporter.ReportePDFExporter;
import com.laboratorio.model.ExamenTop;
import com.laboratorio.service.ReporteService;
import com.laboratorio.service.TopExamenReporteService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardPageController {

    private final ReporteService reporteService;
    private final TopExamenReporteService topExamenReporteService;

    public DashboardPageController(ReporteService reporteService,
            TopExamenReporteService topExamenReporteService) {
        this.reporteService = reporteService;
        this.topExamenReporteService = topExamenReporteService;
    }

    @GetMapping
    public String mostrarDashboard() {
        return "dashboard/dashboard";
    }

    //PDF
    @GetMapping("/reportes/examenes/pdf")
    public void exportarReportePdf(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String estado,
            @RequestParam(name = "examen", required = false) String nombreExamen,
            HttpServletResponse response) throws IOException {

        LocalDate fDesde = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde)
                : LocalDate.now().minusMonths(1);

        LocalDate fHasta = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta)
                : LocalDate.now();

        List<Reporte> datos = reporteService.generarReporte(
                fDesde, fHasta, area, estado, nombreExamen
        );

        ReportePDFExporter exporter = new ReportePDFExporter(datos);
        exporter.exportPdf(response);
    }

    //Excel
    @GetMapping("/reportes/examenes/excel")
    public void exportarReporteExcel(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String estado,
            @RequestParam(name = "examen", required = false) String nombreExamen,
            HttpServletResponse response) throws IOException {

        LocalDate fDesde = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde)
                : LocalDate.now().minusMonths(1);

        LocalDate fHasta = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta)
                : LocalDate.now();

        List<Reporte> datos = reporteService.generarReporte(
                fDesde, fHasta, area, estado, nombreExamen
        );

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=ReporteExamenes.xlsx");

        reporteService.exportarExcel(datos, response.getOutputStream());
    }

    //Grafico PDF
    @PostMapping("/top-examenes/pdf-imagen")
    public void exportarTopExamenesPdfImagen(
            @RequestParam("chartImage") String chartImage,
            HttpServletResponse response) throws IOException {

        String base64 = chartImage;
        int commaIndex = base64.indexOf(',');
        if (commaIndex != -1) {
            base64 = base64.substring(commaIndex + 1);
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=TopExamenesGrafico.pdf");

        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Top de exámenes solicitados", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Image chartImg = Image.getInstance(imageBytes);

            chartImg.scaleToFit(
                    PageSize.A4.getWidth() - 80,
                    PageSize.A4.getHeight() - 120
            );
            chartImg.setAlignment(Image.ALIGN_CENTER);

            document.add(chartImg);

        } catch (DocumentException e) {
            throw new IOException("Error al generar PDF con la imagen del gráfico", e);
        } finally {
            document.close();
        }
    }

    //Grafico Excel
    @GetMapping("/top-examenes/excel")
    public void exportarTopExamenesExcel(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String area,
            @RequestParam(required = false, defaultValue = "10") Integer limite,
            HttpServletResponse response) throws IOException {

        LocalDate fDesde = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde)
                : LocalDate.now().minusMonths(1);

        LocalDate fHasta = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta)
                : LocalDate.now();

        List<ExamenTop> datos = topExamenReporteService.generarTop(
                fDesde, fHasta, area, limite != null ? limite : 10
        );

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=TopExamenes.xlsx");

        topExamenReporteService.exportarExcel(datos, response.getOutputStream());
    }

}
