/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Examen;
import com.laboratorio.model.ExamenInsumo;
import com.laboratorio.model.ExamenInsumoForm;
import com.laboratorio.model.Insumo;
import com.laboratorio.service.ExamenInsumoService;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.InsumoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/examen")
public class ExamenController {

    private final ExamenService examenService;
    private final ExamenInsumoService examenInsumoService;
    private final InsumoService insumoService;

    @Autowired
    public ExamenController(ExamenService examenService,
            ExamenInsumoService examenInsumoService,
            InsumoService insumoService) {
        this.examenService = examenService;
        this.examenInsumoService = examenInsumoService;
        this.insumoService = insumoService;
    }

    @GetMapping("/examenes")
    public String listadoExamenes(Model model) {
        model.addAttribute("examenes", examenService.getAll());
        return "examen/examenes";
    }

    @GetMapping("/agregar")
    public String agregarExamen(Model model) {
        model.addAttribute("examen", new Examen());
        return "examen/agregar";
    }

    @PostMapping("/guardar")
    public String guardarExamen(
            @Valid @ModelAttribute("examen") Examen examen,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean esNuevo = (examen.getIdExamen() == null);

        if (result.hasErrors()) {
            model.addAttribute("examen", examen);
            model.addAttribute("errores", result.getAllErrors());
            return esNuevo ? "examen/agregar" : "examen/modificar";
        }

        try {

            if (examenService.existsByNombre(examen.getNombre())) {

                if (esNuevo) {
                    throw new IllegalArgumentException("El examen ya existe");
                } else {
                    Examen existente = examenService.get(examen);

                    if (!examen.getNombre().equalsIgnoreCase(existente.getNombre())) {
                        throw new IllegalArgumentException("El examen ya existe");
                    }
                }
            }

            if (examen.getValorMinimo() > examen.getValorMaximo()) {
                throw new IllegalArgumentException("El valor mínimo no puede ser mayor que el valor máximo");
            }

            examenService.save(examen);

            redirectAttributes.addFlashAttribute("success",
                    esNuevo ? "Examen registrado correctamente" : "Examen actualizado correctamente");

            return "redirect:/examen/examenes";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("examen", examen);
            model.addAttribute("error", ex.getMessage());

            return esNuevo ? "examen/agregar" : "examen/modificar";
        }
    }

    @PostMapping("/actualizar")
    public String actualizarExamen(
            @Valid @ModelAttribute("examen") Examen examen,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("errores", result.getAllErrors());
            model.addAttribute("examen", examen);
            return "examen/modificar";
        }

        try {
            Examen existente = examenService.get(examen);

            // Unicidad
            if (!examen.getNombre().equalsIgnoreCase(existente.getNombre())
                    && examenService.existsByNombre(examen.getNombre())) {
                throw new IllegalArgumentException("El examen ya existe");
            }

            if (examen.getValorMinimo() > examen.getValorMaximo()) {
                throw new IllegalArgumentException("El valor mínimo no puede ser mayor que el valor máximo");
            }

            existente.setNombre(examen.getNombre());
            existente.setArea(examen.getArea());
            existente.setPrecio(examen.getPrecio());
            existente.setCondiciones(examen.getCondiciones());
            existente.setUnidad(examen.getUnidad());
            existente.setValorMinimo(examen.getValorMinimo());
            existente.setValorMaximo(examen.getValorMaximo());
            existente.setActivo(examen.getActivo());

            examenService.save(existente);

            redirectAttributes.addFlashAttribute("success", "Examen actualizado correctamente");
            return "redirect:/examen/examenes";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("examen", examen);
            return "examen/modificar";
        }
    }

    @GetMapping("/modificar/{idExamen}")
    public String modificarExamen(Examen examen, Model model) {
        examen = examenService.get(examen);
        model.addAttribute("examen", examen);
        return "examen/modificar";
    }

    @GetMapping("/eliminar/{idExamen}")
    public String eliminarExamen(Examen examen) {
        examenService.delete(examen);
        return "redirect:/examen/examenes";
    }

    @GetMapping("/buscar/json")
    @ResponseBody
    public List<Examen> buscarExamenesJson(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return examenService.getAll();
        } else {
            return examenService.buscarExamenes(query.trim());
        }
    }

    @GetMapping("/proforma")
    public String listadoProforma(Model model) {
        model.addAttribute("examenes", examenService.getActivos());
        return "examen/proforma";
    }

    @PostMapping("/mostrar")
    public String mostrarProforma(@RequestParam("seleccionados") List<Long> ids, Model model) {
        List<Examen> seleccionados = examenService.findById(ids);
        double total = seleccionados.stream()
                .map(Examen::getPrecio)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        System.out.println(seleccionados);
        model.addAttribute("examenes", seleccionados);
        model.addAttribute("total", total);
        model.addAttribute("fechaActual", LocalDate.now());
        return "examen/mostrar-proforma";
    }

    @GetMapping("/{idExamen}/insumos")
    public String gestionarInsumosExamen(@PathVariable Long idExamen, Model model) {
        Examen examen = new Examen();
        examen.setIdExamen(idExamen);
        examen = examenService.get(examen);

        if (examen == null) {
            return "redirect:/examen/examenes";
        }

        List<ExamenInsumo> relaciones = examenInsumoService.listarPorExamen(idExamen);

        Map<Long, Insumo> insumosRelacionados = new HashMap<>();
        for (ExamenInsumo relacion : relaciones) {
            Insumo insumo = new Insumo();
            insumo.setIdInsumo(relacion.getIdInsumo());
            insumo = insumoService.get(insumo);

            if (insumo != null) {
                insumosRelacionados.put(relacion.getIdInsumo(), insumo);
            }
        }

        model.addAttribute("examen", examen);
        model.addAttribute("relaciones", relaciones);
        model.addAttribute("insumosRelacionados", insumosRelacionados);
        model.addAttribute("insumos", insumoService.getAll().stream().filter(Insumo::isActivo).toList());
        model.addAttribute("examenInsumoForm", new ExamenInsumoForm());

        return "examen/examen_insumo";
    }

    @PostMapping("/{idExamen}/insumos/guardar")
    public String guardarRelacionInsumo(@PathVariable Long idExamen,
            @ModelAttribute("examenInsumoForm") ExamenInsumoForm form,
            RedirectAttributes redirectAttributes) {

        try {
            examenInsumoService.guardarRelacion(idExamen, form.getIdInsumo(), form.getCantidadNecesaria());
            redirectAttributes.addFlashAttribute("success", "Relación guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }

        return "redirect:/examen/" + idExamen + "/insumos";
    }

    @GetMapping("/{idExamen}/insumos/eliminar/{idExamenInsumo}")
    public String eliminarRelacionInsumo(@PathVariable Long idExamen,
            @PathVariable Long idExamenInsumo,
            RedirectAttributes redirectAttributes) {

        examenInsumoService.eliminarRelacion(idExamenInsumo);
        redirectAttributes.addFlashAttribute("success", "Relación eliminada correctamente.");

        return "redirect:/examen/" + idExamen + "/insumos";
    }
}
