/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Paciente;
import com.laboratorio.repository.PacienteRepository;
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
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteController(PacienteService pacienteService, PacienteRepository pacienteRepository) {
        this.pacienteService = pacienteService;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/pacientes")
    public String listadoPacientes(Model model) {

        List<Paciente> pacientes = pacienteService.getPacientesActivos();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("page", "list");

        return "paciente/pacientes";
    }

    @GetMapping("/agregar")
    public String agregarPaciente(Model model) {
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("page", "create");
        return "paciente/agregar"; //
    }

    @PostMapping("/guardar")
    public String guardarPaciente(
            @Valid @ModelAttribute("paciente") Paciente paciente,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean esNuevo = (paciente.getIdPaciente() == null);

        if (result.hasErrors()) {
            model.addAttribute("errores", result.getAllErrors());
            model.addAttribute("page", "create");  // muy importante
            return "/paciente/agregar";
        }

        try {
            if (esNuevo) {
                if (pacienteRepository.existsByCedula(paciente.getCedula())) {
                    throw new IllegalArgumentException("La cédula ya está registrada");
                }
                if (pacienteRepository.existsByTelefono(paciente.getTelefono())) {
                    throw new IllegalArgumentException("El teléfono ya está registrado");
                }
                if (paciente.getEmail() != null && !paciente.getEmail().isBlank()
                        && pacienteRepository.existsByEmail(paciente.getEmail())) {
                    throw new IllegalArgumentException("El correo ya está registrado");
                }
            } else {
                Paciente existente = pacienteRepository.findById(paciente.getIdPaciente())
                        .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

                if (!paciente.getCedula().equals(existente.getCedula())
                        && pacienteRepository.existsByCedula(paciente.getCedula())) {
                    throw new IllegalArgumentException("La cédula ya está registrada");
                }
                if (!paciente.getTelefono().equals(existente.getTelefono())
                        && pacienteRepository.existsByTelefono(paciente.getTelefono())) {
                    throw new IllegalArgumentException("El teléfono ya está registrado");
                }
                if (paciente.getEmail() != null && !paciente.getEmail().isBlank()
                        && !paciente.getEmail().equals(existente.getEmail())
                        && pacienteRepository.existsByEmail(paciente.getEmail())) {
                    throw new IllegalArgumentException("El correo ya está registrado");
                }
            }

            if (esNuevo) {
                String anio = new SimpleDateFormat("yy").format(new Date());
                int maxSeq = pacienteService.getMaxSequenceForYear(anio);
                paciente.setIdPaciente("P" + anio + "-" + String.format("%04d", maxSeq + 1));
            }

            pacienteService.save(paciente);
            redirectAttributes.addFlashAttribute("success", "Paciente registrado correctamente");
            return "redirect:/paciente/pacientes";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("paciente", paciente);
            model.addAttribute("page", "create"); // clave para mostrar el form
            return "/paciente/agregar";
        }
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
    public String actualizarPaciente(
            @Valid @ModelAttribute("paciente") Paciente paciente,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Paciente existente = pacienteService.getPaciente(paciente.getIdPaciente());
        if (existente == null) {
            return "redirect:/paciente/pacientes";
        }
        
        if (result.hasErrors()) {
            model.addAttribute("errores", result.getAllErrors());
            model.addAttribute("paciente", paciente);
            model.addAttribute("page", "edit");
            return "/paciente/editar";
        }

        try {
            if (!paciente.getCedula().equals(existente.getCedula())
                    && pacienteRepository.existsByCedula(paciente.getCedula())) {
                throw new IllegalArgumentException("La cédula ya está registrada");
            }
            if (!paciente.getTelefono().equals(existente.getTelefono())
                    && pacienteRepository.existsByTelefono(paciente.getTelefono())) {
                throw new IllegalArgumentException("El teléfono ya está registrado");
            }
            if (paciente.getEmail() != null && !paciente.getEmail().isBlank()
                    && !paciente.getEmail().equals(existente.getEmail())
                    && pacienteRepository.existsByEmail(paciente.getEmail())) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }

            existente.setNombre(paciente.getNombre());
            existente.setPrimerApellido(paciente.getPrimerApellido());
            existente.setSegundoApellido(paciente.getSegundoApellido());
            existente.setCedula(paciente.getCedula());
            existente.setTelefono(paciente.getTelefono());
            existente.setEmail(paciente.getEmail());
            existente.setActivo(paciente.getActivo());
            existente.setFechaNacimiento(paciente.getFechaNacimiento());
            existente.setAlergia(paciente.getAlergia());
            existente.setPadecimiento(paciente.getPadecimiento());
            existente.setContactoEmergencia(paciente.getContactoEmergencia());

            pacienteService.save(existente);

            redirectAttributes.addFlashAttribute("success", "Paciente actualizado correctamente");
            return "redirect:/paciente/pacientes";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("paciente", paciente);
            model.addAttribute("page", "edit");
            return "/paciente/editar";
        }
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
