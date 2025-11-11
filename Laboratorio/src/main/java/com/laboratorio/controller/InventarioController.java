/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Inventario;
import com.laboratorio.service.InsumoService;
import com.laboratorio.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/inventario")
public class InventarioController {
      private final InventarioService inventarioService;
      private final InsumoService insumoService; 

    @Autowired
    public InventarioController(InventarioService inventarioService, InsumoService insumoService) {
        this.inventarioService = inventarioService;
        this.insumoService = insumoService; 
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
         model.addAttribute("inventario", new Inventario());
         model.addAttribute("insumos", insumoService.getAll());
         return "/inventario/agregar";
     }
    //modificar
    
     @GetMapping("/modificar/{idInventario}")
     public String modificarInventario(Inventario inventario, Model model){
         inventario = inventarioService.get(inventario);
         model.addAttribute("insumos", insumoService.getAll());
         model.addAttribute("inventario", inventario);
         System.out.println(inventario.toString());
         return "inventario/modificar";
     }
    //guardar
     
      @PostMapping("/guardar")
      public String guardarUsuario(@ModelAttribute Inventario insumo, Model model) {

        try {
            inventarioService.save(insumo);
            return "redirect:/inventario/inventarios";
        } catch (IllegalArgumentException e) {
            
            model.addAttribute("error", e.getMessage());
            return "/inventario/agregar";
        }

    }

}
