package com.laboratorio.controller;

import com.laboratorio.exporter.AuditoriaCriticosPDFExporter;
import com.laboratorio.exporter.AuditoriaPDFExporter;
import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.service.AuditoriaCriticosService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auditoria/criticos")
public class AuditoriaCriticosController {

    private final AuditoriaCriticosService auditoriaCriticosService;

    @Autowired
    public AuditoriaCriticosController(AuditoriaCriticosService auditoriaCriticosService) {
        this.auditoriaCriticosService = auditoriaCriticosService;
    }

    @GetMapping
    public String listarAuditoriasCriticas(Model model) {
        List<AuditoriaCriticos> auditoriasCriticas = auditoriaCriticosService.listarCriticas();
        model.addAttribute("auditorias", auditoriasCriticas);
        model.addAttribute("page", "list");
        return "auditoria/criticos";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public List<AuditoriaCriticos> buscar(@RequestParam("query") String query) {
        List<AuditoriaCriticos> porUsuario = auditoriaCriticosService.buscarPorUsuario(query);
        List<AuditoriaCriticos> porTipo = auditoriaCriticosService.buscarPorTipoEvento(query);

        Set<AuditoriaCriticos> result = new LinkedHashSet<>();
        result.addAll(porUsuario);
        result.addAll(porTipo);
        return new ArrayList<>(result);
    }

    @GetMapping("/buscar/fecha")
    @ResponseBody
    public List<AuditoriaCriticos> buscarPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        return auditoriaCriticosService.buscarPorFecha(inicio, fin);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            HttpServletResponse response) throws IOException {

        if (usuario != null && usuario.isBlank()) {
            usuario = null;
        }
        if (tipoEvento != null && tipoEvento.isBlank()) {
            tipoEvento = null;
        }

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        List<AuditoriaCriticos> lista = auditoriaCriticosService.filtrar(usuario, tipoEvento, inicio, fin);

        AuditoriaCriticosPDFExporter exporter = new AuditoriaCriticosPDFExporter(lista);
        exporter.export(response);
    }

    @GetMapping("/export/excel")
    public void exportarExcel(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            HttpServletResponse response
    ) throws IOException {

        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        List<AuditoriaCriticos> lista = auditoriaCriticosService.filtrar(usuario, tipoEvento, inicio, fin);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Auditoria_Criticos.xlsx");

        auditoriaCriticosService.exportarExcel(lista, response.getOutputStream());

    }
}
