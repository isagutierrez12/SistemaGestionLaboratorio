/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Auditoria;
import com.laboratorio.service.AuditoriaService;
import java.time.LocalDate;
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

    // Listado principal
    @GetMapping("/auditorias")
    public String listarAuditorias(Model model) {
        List<Auditoria> auditorias = auditoriaService.listarTodas();
        model.addAttribute("auditorias", auditorias);
        model.addAttribute("page", "list");
        return "auditoria/auditorias";
    }
    

    // Búsqueda (por usuario, módulo o acción)
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

    // Endpoint JSON opcional para búsquedas dinámicas
    @GetMapping("/buscar/json")
    @ResponseBody
    public List<Auditoria> buscarAuditoriaJson(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return auditoriaService.listarAuditorias();
        } else {
            return auditoriaService.buscarAuditoria(query.trim());
        }
    }

    // Endpoint JSON para buscar solo por fecha
    @GetMapping("/buscar/fecha")
    @ResponseBody
    public List<Auditoria> buscarAuditoriaPorFecha(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return auditoriaService.buscarPorFecha(fechaInicio, fechaFin);
    }

}
