package com.laboratorio.controller;

import com.laboratorio.model.Insumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.service.InsumoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/insumo")
public class InsumoController {

    private final InsumoService insumoService;

    @Autowired
    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping("/insumos")
    public String listadoInsumo(Model model) {
        model.addAttribute("insumos", insumoService.getAll());
        System.out.println(insumoService.getAll().toString());
        return "insumo/insumos";
    }

    @GetMapping("/agregar")
    public String agregarInsumo(Model model) {
        model.addAttribute("insumo", new Insumo());
        return "/insumo/agregar";
    }

    @PostMapping("/guardar")
    public String guardarInsumo(@ModelAttribute Insumo insumo, Model model, RedirectAttributes redirectAttributes) {
        boolean esNuevo = (insumo.getIdInsumo() == null);
        
        
        try {
            insumoService.save(insumo);
            redirectAttributes.addFlashAttribute("mensaje",  esNuevo ? "Insumo registrado correctamente" : "Insumo modificado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/insumo/insumos";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/insumo/agregar";
        }
    }

    @GetMapping("/modificar/{idInsumo}")
    public String modificarInventario(Insumo insumo, Model model) {
        insumo = insumoService.get(insumo);
        model.addAttribute("insumo", insumo);
        return "insumo/modificar";
    }

    @GetMapping("/buscarJSON")
    @ResponseBody
    public List<Insumo> buscarInsumosJSON(
            @RequestParam(value = "query", required = false) String query) {

        if (query == null || query.trim().isEmpty()) {
            return insumoService.getAll();
        }

        return insumoService.buscarPorQuery(query.trim());
    }

}
