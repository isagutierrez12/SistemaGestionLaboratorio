/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Examen;
import com.laboratorio.service.ExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/examen")
public class ExamenController {

    private final ExamenService examenService;

    @Autowired
    public ExamenController(ExamenService examenService) {
        this.examenService = examenService;
    }

    //listado
    //agregar
    @PostMapping("/guardar")
    public String guardarExamen(@ModelAttribute Examen examen, Model model) {
        try {
            examenService.save(examen);
            return "redirect:/examen/listado";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/examen/listado";
        }
    }

    //editar
    @GetMapping("/modificar/{idExamen}")
    public String modificarExamen(Examen entity, Model model) {
        entity = examenService.get(entity);
        model.addAttribute("examen", entity);
        return "examen/modificar";

    }

    //otros
}
