/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.DetallePaquete;
import com.laboratorio.repository.DetallePaqueteRepository;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Paquete;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.PaqueteService;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/paquete")
public class PaqueteController {

    private final ExamenService examenService;
    private final PaqueteService paqueteService;
    private final DetallePaqueteRepository detallePaqueteRepository;

    @Autowired
    public PaqueteController(PaqueteService paqueteService, ExamenService examenService, DetallePaqueteRepository detallePaqueteRepository) {
        this.examenService = examenService;
        this.paqueteService = paqueteService;
        this.detallePaqueteRepository = detallePaqueteRepository;
    }

    //listado
    @GetMapping("/paquetes")
    public String listadoPaquete(Model model) {
        model.addAttribute("paquetes", paqueteService.getAll());
        return "paquete/paquetes";
    }

// Agregar sigue igual
    @GetMapping("/agregar")
    public String agregarPaquete(Model model) {
        model.addAttribute("paquete", new Paquete());
        return "paquete/agregar";
    }

// Guardar
    @PostMapping("/guardar")
    public String guardarPaquete(@ModelAttribute("paquete") Paquete paquete) {
        paqueteService.save(paquete);
        return "redirect:/paquete/paquetes";
    }

// Editar
    @GetMapping("/modificar/{idPaquete}")
    public String modificarPaquete(@PathVariable Long idPaquete, Model model) {
        Paquete p = new Paquete();
        p.setIdPaquete(idPaquete);
        Paquete paquete = paqueteService.get(p);
        model.addAttribute("paquete", paquete);

        List<DetallePaquete> detalles = detallePaqueteRepository.findByPaqueteIdPaquete(idPaquete);
        model.addAttribute("detallesPaquete", detalles);

        // dropdown de exámenes activos
        List<Examen> examenesActivos = examenService.getAll().stream()
                .filter(Examen::isActivo)
                .collect(Collectors.toList());
        model.addAttribute("examenesActivos", examenesActivos);

        return "paquete/modificar";
    }

// Eliminar
    @GetMapping("/eliminar/{idPaquete}")
    public String eliminarPaquete(@PathVariable Long idPaquete) {
        Paquete p = new Paquete();
        p.setIdPaquete(idPaquete);
        paqueteService.delete(p);
        return "redirect:/paquete/paquetes";
    }

// Buscar
    @GetMapping("/buscar")
    public String buscarPaquete(@RequestParam("query") String query, Model model) {
        model.addAttribute("paquetes", paqueteService.buscarPaquetes(query));
        model.addAttribute("query", query);
        return "paquete/paquetes";
    }

    //otros
    //getionar los examenes en modificar paquete
    @PostMapping("/{idPaquete}/agregar-examen")
    public String agregarExamen(@PathVariable Long idPaquete, @RequestParam("idExamen") Long idExamen) {
        paqueteService.agregarExamen(idPaquete, idExamen);
        return "redirect:/paquete/modificar/" + idPaquete;
    }

    @PostMapping("/{idPaquete}/quitar-examen")
    public String quitarExamen(@PathVariable Long idPaquete, @RequestParam("idExamen") Long idExamen) {
        paqueteService.quitarExamen(idPaquete, idExamen);
        return "redirect:/paquete/modificar/" + idPaquete;
    }
}
