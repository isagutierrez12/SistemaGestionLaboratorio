package com.laboratorio.controller;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.service.AuditoriaCriticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

}
