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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    @Autowired
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Listado
    @GetMapping("/pacientes")
    public String listadoPacientes(Model model) {

        List<Paciente> pacientes = pacienteService.getPacientesActivos();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("page", "list");

        return "paciente/pacientes";
    }

// Agregar
    @GetMapping("/agregar")
    public String agregarPaciente(Model model) {
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("page", "create");
        return "paciente/agregar"; //
    }

// Guardar
    @PostMapping("/guardar")
    public String guardarPaciente(@ModelAttribute("paciente") Paciente paciente) {
        paciente.setFechaCreacion(new Date());

        String anio = new SimpleDateFormat("yy").format(paciente.getFechaCreacion());
        int maxSeq = pacienteService.getMaxSequenceForYear(anio);
        paciente.setIdPaciente("P" + anio + "-" + String.format("%04d", maxSeq + 1));

        pacienteService.save(paciente);
        return "redirect:/paciente/pacientes";
    }

    @GetMapping("/buscar")
    public String buscarPacientes(@RequestParam("query") String query, Model model) {
        List<Paciente> pacientes;

        if (query == null || query.trim().isEmpty()) {
            pacientes = pacienteService.getPacientesActivos();
        } else {
            pacientes = pacienteService.buscarPacientes(query.trim());
        }

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("query", query);
        model.addAttribute("page", "list");
        return "paciente/pacientes";
    }

    @GetMapping("/buscar/json")
    @ResponseBody
    public List<Paciente> buscarPacientesJson(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return pacienteService.getPacientesActivos();
        } else {
            return pacienteService.buscarPacientes(query.trim());
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPaciente(@PathVariable("id") String id, Model model) {
        Paciente paciente = pacienteService.getPaciente(id);
        if (paciente == null) {
            return "redirect:/paciente/pacientes";
        }
        model.addAttribute("paciente", paciente);
        model.addAttribute("page", "edit");
        return "/paciente/editar";
    }

    @PostMapping("/actualizar")
    public String actualizarPaciente(@ModelAttribute("paciente") Paciente paciente) {
        Paciente p = pacienteService.getPaciente(paciente.getIdPaciente());
        if (p == null) {
            return "redirect:/paciente/pacientes";
        }

        p.setNombre(paciente.getNombre());
        p.setPrimerApellido(paciente.getPrimerApellido());
        p.setSegundoApellido(paciente.getSegundoApellido());
        p.setCedula(paciente.getCedula());
        p.setTelefono(paciente.getTelefono());
        p.setEmail(paciente.getEmail());
        p.setActivo(paciente.isActivo());
        p.setFechaNacimiento(paciente.getFechaNacimiento());
        p.setAlergia(paciente.getAlergia());
        p.setPadecimiento(paciente.getPadecimiento());
        p.setContactoEmergencia(paciente.getContactoEmergencia());

        pacienteService.save(p);
        return "redirect:/paciente/pacientes";
    }

    @GetMapping("/inactivos")
    public String listadoPacientesInactivos(Model model) {
        List<Paciente> pacientes = pacienteService.getPacientesInactivos();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("page", "inactive");
        return "paciente/inactivos";
    }

    @GetMapping("/inactivos/buscar")
    public String buscarPacientesInactivos(@RequestParam("query") String query, Model model) {
        List<Paciente> pacientes;

        if (query == null || query.trim().isEmpty()) {
            pacientes = pacienteService.getPacientesInactivos();
        } else {
            pacientes = pacienteService.buscarPacientesInactivos(query.trim());
        }

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("query", query);
        model.addAttribute("page", "inactive");
        return "paciente/inactivos";
    }

}
