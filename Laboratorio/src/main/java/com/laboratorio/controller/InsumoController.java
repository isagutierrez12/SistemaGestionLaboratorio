/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Insumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.service.InsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/insumo")
public class InsumoController {
    private final InsumoService insumoService; 
    @Autowired
    public InsumoController(InsumoService insumoService){
        this.insumoService = insumoService; 
    }
    
    //listado
    @GetMapping("/insumos")
    public String listadoInsumo(Model model){
        model.addAttribute("insumos", insumoService.getAll());
        System.out.println(insumoService.getAll().toString());
        return "insumo/insumos";
    }
    //agregar
       @GetMapping("/agregar")
     public String agregarInsumo(Model model){
          model.addAttribute("insumo", new Insumo());
        return "/insumo/agregar";
     }
    //moidificar
      @PostMapping("/guardar")
      public String guardarInsumo(@ModelAttribute Insumo insumo, Model model){
            try {
            insumoService.save(insumo);
            return "redirect:/insumo/insumos";
        } catch (IllegalArgumentException e) {
            
            model.addAttribute("error", e.getMessage());
            return "/insumo/agregar";
        }
      }
      
     //modificar
        @GetMapping("/modificar/{idInsumo}")
     public String modificarInventario(Insumo insumo, Model model){
         insumo = insumoService.get(insumo);
         model.addAttribute("insumo", insumo);
         return "insumo/modificar";
     }
    
}
