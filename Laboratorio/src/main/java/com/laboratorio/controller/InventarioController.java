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
    public String modificarInventario(Inventario inventario, Model model) {
        inventario = inventarioService.get(inventario);
        model.addAttribute("insumos", insumoService.getAll());
        model.addAttribute("inventario", inventario);
        System.out.println(inventario.toString());
        return "inventario/modificar";
    }

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
