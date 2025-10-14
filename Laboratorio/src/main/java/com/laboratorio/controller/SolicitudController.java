package com.laboratorio.controller;
import com.laboratorio.model.Solicitud;
import com.laboratorio.service.SolicitudService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    private final SolicitudService solicitudService;

    @Autowired
    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Listado de solicitudes
    @GetMapping("/solicitudes")
    public String listadoSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.getAll());
        return "solicitud/solicitudes"; // Debes crear esta vista
    }

    // Agregar solicitud
    @GetMapping("/agregar")
    public String agregarSolicitud(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        return "solicitud/agregar"; // Debes crear esta vista
    }

    // Guardar solicitud
    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute("solicitud") Solicitud solicitud) {
        solicitudService.save(solicitud);
        return "redirect:/solicitud/solicitudes";
    }

    // Editar solicitud
    @GetMapping("/modificar/{idSolicitud}")
    public String modificarSolicitud(Solicitud solicitud, Model model) {
        solicitud = solicitudService.get(solicitud);
        model.addAttribute("solicitud", solicitud);
        return "solicitud/modificar"; 
    }

    // Eliminar solicitud
    @GetMapping("/eliminar/{idSolicitud}")
    public String eliminarSolicitud(Solicitud solicitud) {
        solicitudService.delete(solicitud);
        return "redirect:/solicitud/solicitudes";
    }

    // Buscar solicitudes
    @GetMapping("/buscar")
    public String buscarSolicitudes(@RequestParam("query") String query, Model model) {
        List<Solicitud> solicitudes;

        if (query == null || query.trim().isEmpty()) {
            solicitudes = solicitudService.getAll();
        } else {
            solicitudes = solicitudService.buscarSolicitudes(query.trim());
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("query", query); 
        return "solicitud/solicitudes"; 
    }
}
