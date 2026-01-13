package com.laboratorio.controller;

import com.laboratorio.exporter.AuditoriaPDFExporter;
import com.laboratorio.model.Auditoria;
import com.laboratorio.service.AuditoriaService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @Autowired
    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /*
    @GetMapping("/auditorias")
    public String listarAuditorias(
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model
    ) {
        System.out.println("ENTRÓ AL CONTROLADOR /auditorias page=" + pageNumber);

        int pageSize = 6;

        List<Auditoria> todas = auditoriaService.listarTodas();

        int totalItems = todas.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<Auditoria> auditoriasPagina = todas.subList(fromIndex, toIndex);

        model.addAttribute("auditorias", auditoriasPagina);
        model.addAttribute("page", "list");

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);

        return "auditoria/auditorias";
    }
*/
      @GetMapping("/auditorias")
    public String listarAuditorias(Model model) {
        List<Auditoria> auditorias = auditoriaService.listarTodas();
        model.addAttribute("auditorias", auditorias);
        model.addAttribute("page", "list");
        return "auditoria/auditorias";
    }

    @GetMapping("/buscar")
    public String buscarAuditoria(@RequestParam("query") String query, Model model) {
        List<Auditoria> auditorias;

        if (query == null || query.trim().isEmpty()) {
            auditorias = auditoriaService.listarAuditorias();
        } else {
            auditorias = auditoriaService.buscarAuditoria(query.trim());
        }

        model.addAttribute("auditorias", auditorias);
        model.addAttribute("query", query);
        model.addAttribute("page", "list");
        return "auditoria/listado";
    }

    @GetMapping("/buscar/json")
    @ResponseBody
    public List<Auditoria> buscarAuditoriaJson(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return auditoriaService.listarAuditorias();
        } else {
            return auditoriaService.buscarAuditoria(query.trim());
        }
    }

    @GetMapping("/buscar/fecha")
    @ResponseBody
    public List<Auditoria> buscarAuditoriaPorFecha(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return auditoriaService.buscarPorFecha(fechaInicio, fechaFin);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            HttpServletResponse response) throws IOException {

        if (usuario != null && usuario.isBlank()) {
            usuario = null;
        }
        if (modulo != null && modulo.isBlank()) {
            modulo = null;
        }
        if (accion != null && accion.isBlank()) {
            accion = null;
        }

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        List<Auditoria> lista = auditoriaService.filtrar(usuario, modulo, accion, inicio, fin);

        AuditoriaPDFExporter exporter = new AuditoriaPDFExporter(lista);
        exporter.export(response);
    }

    @GetMapping("/export/excel")
    public void exportarExcel(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            HttpServletResponse response
    ) throws IOException {

        LocalDateTime inicioDT = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime finDT = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        List<Auditoria> auditorias = auditoriaService.filtrar(
                usuario, modulo, accion, inicioDT, finDT
        );

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Auditoria_General.xlsx");

        auditoriaService.exportarExcel(auditorias, response.getOutputStream());
    }

}
