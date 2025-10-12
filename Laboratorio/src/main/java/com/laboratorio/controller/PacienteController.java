/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Paciente;
import com.laboratorio.service.PacienteService;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    //listado
    @GetMapping("/pacientes")
    public String listadoPacientes(Model model) {
        List<Paciente> pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        return "paciente/pacientes";
    }

    //agregar
    @GetMapping("/agregar")
    public String agregarPaciente(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "paciente/agregar"; //
    }

    @PostMapping("/guardar")
    public String guardarPaciente(@ModelAttribute("paciente") Paciente paciente) {
    paciente.setFechaCreacion(new Date());

    String anio = new SimpleDateFormat("yy").format(paciente.getFechaCreacion());
    int maxSeq = pacienteService.getMaxSequenceForYear(anio);
    paciente.setIdPaciente("P" + anio + "-" + String.format("%04d", maxSeq + 1));

    pacienteService.save(paciente);
    return "redirect:/paciente/pacientes";
}
    //otros
    @GetMapping("/buscar")
    public String buscarPacientes(@RequestParam("query") String query, Model model) {
    List<Paciente> pacientes;

    if (query == null || query.trim().isEmpty()) {
        pacientes = pacienteService.getPacientes();
    } else {
        pacientes = pacienteService.buscarPacientes(query.trim());
    }

    model.addAttribute("pacientes", pacientes);
    model.addAttribute("query", query); 
    return "paciente/pacientes"; 
}


}
