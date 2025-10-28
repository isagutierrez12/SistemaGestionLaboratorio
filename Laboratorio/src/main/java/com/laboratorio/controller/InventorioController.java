/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Inventario;
import com.laboratorio.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/inventario")
public class InventorioController {
      private final InventarioService inventarioService;

    @Autowired
    public InventorioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    
    //listado
     @GetMapping("/inventarios")
    public String listadoExamenes(Model model) {
        model.addAttribute("inventarios", inventarioService.getAll());
        return "inventario/inventarios";
    }
    
    //agregar
     @GetMapping("/agregar")
     public String agregarInsumo(Model model){
         model.addAttribute("insumo", new Inventario());
         return "/inventario/agregar";
     }
    //modificar
    
    //guardar
     
      public String guardarUsuario(@ModelAttribute Inventario insumo, Model model) {

        try {
            inventarioService.save(insumo);
            return "redirect:/inventario/inventarios";
        } catch (IllegalArgumentException e) {
            
            model.addAttribute("error", e.getMessage());
            return "/usuario/agregar";
        }

    }
    
    //desactivar
}
