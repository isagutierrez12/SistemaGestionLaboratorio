/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Examen;
import com.laboratorio.service.ExamenService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/examen")
public class ExamenController {

    private final ExamenService examenService;

    @Autowired
    public ExamenController(ExamenService examenService) {
        this.examenService = examenService;
    }

    //listado
    @GetMapping("/examenes")
    public String listadoExamenes(Model model) {
        model.addAttribute("examenes", examenService.findAll());
        return "examen/examenes";
    }

// Agregar sigue igual
    @GetMapping("/agregar")
    public String agregarExamen(Model model) {
        model.addAttribute("examen", new Examen());
        return "examen/agregar";
    }

// Guardar
    @PostMapping("/guardar")
    public String guardarExamen(@ModelAttribute("examen") Examen examen) {
        if (examen.getCodigo() == null || examen.getCodigo().isEmpty()) {
            examen.setCodigo("EX" + System.currentTimeMillis());
        }
        examenService.save(examen);
        return "redirect:/examen/examenes";
    }

// Editar
    @GetMapping("/modificar/{idExamen}")
    public String modificarExamen(@PathVariable Long idExamen, Model model) {
        Examen examen = examenService.findById(idExamen);
        model.addAttribute("examen", examen);
        return "examen/modificar";
    }

// Eliminar
    @GetMapping("/eliminar/{idExamen}")
    public String eliminarExamen(@PathVariable Long idExamen) {
        examenService.delete(idExamen);
        return "redirect:/examen/examenes";
    }
    
// Buscar
    @GetMapping("/buscar")
    public String buscarExamenes(@RequestParam("query") String query, Model model) {
        List<Examen> examenes;

        if (query == null || query.trim().isEmpty()) {
            examenes = examenService.findAll();
        } else {
            examenes = examenService.buscarExamenes(query.trim());
        }

        model.addAttribute("examenes", examenes);
        model.addAttribute("query", query); 
        return "examen/examenes"; 
    }


    //otros
}
