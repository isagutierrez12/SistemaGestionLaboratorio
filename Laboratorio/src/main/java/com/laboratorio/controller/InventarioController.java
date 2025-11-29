package com.laboratorio.controller;

import com.laboratorio.exporter.InventarioPDFExporter;
import com.laboratorio.model.Inventario;
import com.laboratorio.service.InsumoService;
import com.laboratorio.service.InventarioService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/inventarios")
    public String listadoExamenes(Model model) {
        model.addAttribute("inventarios", inventarioService.getAll());

        return "inventario/inventarios";
    }

    @GetMapping("/agregar")
    public String agregarInsumo(Model model) {
        model.addAttribute("inventario", new Inventario());
        model.addAttribute("insumos", insumoService.getAll());
        return "/inventario/agregar";
    }

    @GetMapping("/modificar/{idInventario}")
    public String modificarInventario(@PathVariable Long idInventario, Model model) {
        Inventario inventario = inventarioService.get(new Inventario() {
            {
                setIdInventario(idInventario);
            }
        });

        if (inventario == null) {
            model.addAttribute("error", "Inventario no encontrado");
            return "redirect:/inventario/inventarios";
        }

        model.addAttribute("insumos", insumoService.getAll());
        model.addAttribute("inventario", inventario);
        return "inventario/modificar";
    }

    @PostMapping("/guardar")
    public String guardarInventario(@ModelAttribute Inventario inventario,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean esNuevo = (inventario.getIdInventario() == null); 

        try {
            if (!esNuevo) {
                Inventario original = inventarioService.get(new Inventario() {
                    {
                        setIdInventario(inventario.getIdInventario());
                    }
                });

                if (original != null) {
                    if (inventario.getFechaVencimiento() == null) {
                        inventario.setFechaVencimiento(original.getFechaVencimiento());
                    }
                    if (inventario.getFechaApertura() == null) {
                        inventario.setFechaApertura(original.getFechaApertura());
                    }
                }
            }

            inventarioService.save(inventario);

            redirectAttributes.addFlashAttribute("success",
                    esNuevo
                            ? "Inventario registrado correctamente."
                            : "Inventario modificado correctamente."
            );

            return "redirect:/inventario/inventarios";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("insumos", insumoService.getAll());
            model.addAttribute("inventario", inventario);

            return esNuevo ? "/inventario/agregar" : "/inventario/modificar";
        }
    }

    @GetMapping("/buscarJSON")
    @ResponseBody
    public List<Inventario> buscarInventarioJSON(@RequestParam(value = "query", required = false) String query) {

        if (query == null || query.trim().isEmpty()) {
            return inventarioService.getAll();
        }

        return inventarioService.buscarInventarioPorQuery(query.trim());
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<Inventario> lista = inventarioService.getAll();

        InventarioPDFExporter exporter = new InventarioPDFExporter(lista);
        exporter.export(response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Inventario> lista = inventarioService.getAll();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Inventario.xlsx");

        inventarioService.exportarExcel(lista, response.getOutputStream());
    }

}
