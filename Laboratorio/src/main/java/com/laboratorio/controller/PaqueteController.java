package com.laboratorio.controller;

import com.laboratorio.model.DetallePaquete;
import com.laboratorio.repository.DetallePaqueteRepository;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Paquete;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.PaqueteService;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/paquete")
public class PaqueteController {

    private final ExamenService examenService;
    private final PaqueteService paqueteService;
    private final DetallePaqueteRepository detallePaqueteRepository;
    private final SolicitudDetalleRepository solicitudDetalleRepository;

    @Autowired
    public PaqueteController(PaqueteService paqueteService, ExamenService examenService,
            DetallePaqueteRepository detallePaqueteRepository,
            SolicitudDetalleRepository solicitudDetalleRepository) {
        this.examenService = examenService;
        this.paqueteService = paqueteService;
        this.detallePaqueteRepository = detallePaqueteRepository;
        this.solicitudDetalleRepository = solicitudDetalleRepository;
    }

    @GetMapping("/paquetes")
    public String listadoPaquete(Model model) {
        model.addAttribute("paquetes", paqueteService.getAll());
        return "paquete/paquetes";
    }

    @GetMapping("/agregar")
    public String agregarPaquete(Model model) {
        model.addAttribute("paquete", new Paquete());
        return "paquete/agregar";
    }

    @PostMapping("/guardar")
    public String guardarPaquete(@ModelAttribute("paquete") @Valid Paquete paquete,
            BindingResult result,
            @RequestParam(value = "examenesSeleccionados", required = false) String examenesSeleccionados,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean esNuevo = (paquete.getIdPaquete() == null);

        if (result.hasErrors()) {
            model.addAttribute("paquete", paquete);
            model.addAttribute("error", "Hay campos inválidos. Revise la información.");

            if (!esNuevo) {
                cargarDatosEdicion(paquete.getIdPaquete(), model);
            }

            return esNuevo ? "paquete/agregar" : "paquete/modificar";
        }

        try {
            if (!esNuevo && Boolean.FALSE.equals(paquete.getActivo())) {
                Paquete existente = paqueteService.getById(paquete.getIdPaquete());
                if (existente != null && Boolean.TRUE.equals(existente.getActivo())) {
                    Long usos = solicitudDetalleRepository.contarCitasAgendadasConPaquete(paquete.getIdPaquete());
                    if (usos != null && usos > 0) {
                        throw new IllegalArgumentException(
                                "No se puede desactivar el paquete porque está incluido en citas agendadas. "
                                + "Finalice o cancele esas citas antes de desactivarlo.");
                    }
                }
            }

            paqueteService.save(paquete);

            if (!esNuevo && examenesSeleccionados != null) {
                paqueteService.actualizarExamenesDelPaquete(paquete.getIdPaquete(), examenesSeleccionados);
            }

        } catch (IllegalArgumentException ex) {
            model.addAttribute("paquete", paquete);
            model.addAttribute("error", ex.getMessage());

            if (!esNuevo) {
                cargarDatosEdicion(paquete.getIdPaquete(), model);
            }

            return esNuevo ? "paquete/agregar" : "paquete/modificar";
        }

        redirectAttributes.addFlashAttribute("success",
                esNuevo ? "Paquete registrado correctamente." : "Paquete actualizado correctamente.");

        return "redirect:/paquete/paquetes";
    }

    private void cargarDatosEdicion(Long idPaquete, Model model) {
        List<DetallePaquete> detalles = detallePaqueteRepository.findByPaqueteIdPaquete(idPaquete);
        model.addAttribute("detallesPaquete", detalles);

        List<Examen> examenesActivos = examenService.getAll().stream()
                .filter(Examen::getActivo)
                .collect(Collectors.toList());
        model.addAttribute("examenesActivos", examenesActivos);
    }

    @GetMapping("/modificar/{idPaquete}")
    public String modificarPaquete(@PathVariable Long idPaquete, Model model) {
        Paquete p = new Paquete();
        p.setIdPaquete(idPaquete);
        Paquete paquete = paqueteService.get(p);
        model.addAttribute("paquete", paquete);

        List<DetallePaquete> detalles = detallePaqueteRepository.findByPaqueteIdPaquete(idPaquete);
        model.addAttribute("detallesPaquete", detalles);

        List<Examen> examenesActivos = examenService.getAll().stream()
                .filter(Examen::getActivo)
                .collect(Collectors.toList());
        model.addAttribute("examenesActivos", examenesActivos);

        return "paquete/modificar";
    }

    @GetMapping("/eliminar/{idPaquete}")
    public String eliminarPaquete(@PathVariable Long idPaquete) {
        Paquete p = new Paquete();
        p.setIdPaquete(idPaquete);
        paqueteService.delete(p);
        return "redirect:/paquete/paquetes";
    }

    @GetMapping("/buscar/json")
    @ResponseBody
    public List<Paquete> buscarPaquetesJson(@RequestParam("query") String query) {
        if (query == null || query.trim().isEmpty()) {
            return paqueteService.getAll();
        } else {
            return paqueteService.buscarPaquetes(query.trim());
        }
    }

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