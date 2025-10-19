package com.laboratorio.controller;
import com.laboratorio.model.Paciente;
import com.laboratorio.model.Solicitud;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.PacienteService;
import com.laboratorio.service.SolicitudService;
import com.laboratorio.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final PacienteService pacienteService;
    private final ExamenService examenService;
    private final UsuarioService usuarioService;
    //private final PaqueteService paqueteService;

    @Autowired
    public SolicitudController(
            SolicitudService solicitudService,
            PacienteService pacienteService,
            ExamenService examenService,
            UsuarioService usuarioService) 
    {
        this.solicitudService = solicitudService;
        this.pacienteService = pacienteService;
        this.examenService = examenService;
        this.usuarioService = usuarioService;
    }

    // Listado de solicitudes
    @GetMapping("/solicitudes")
    public String listadoSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.getAll());
        return "solicitud/solicitudes";
    }

    // Agregar solicitud
    @GetMapping("/agregar")
    public String agregarSolicitud(Model model) {
        model.addAttribute("solicitud", new Solicitud());
        model.addAttribute("pacientes", pacienteService.getAll());
        model.addAttribute("examenes", examenService.getAll());
        return "solicitud/agregar";
    }

    // Guardar solicitud
        // Guardar solicitud (manejo de los escenarios)
   @PostMapping("/guardar")
public String guardarSolicitud(
        @ModelAttribute("solicitud") Solicitud solicitud,
        @RequestParam(required = false) List<Long> examenesSeleccionados,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttrs,
        Model model) {

    // Validación: paciente y al menos un examen
    if (solicitud.getPaciente() == null || solicitud.getPaciente().getIdPaciente() == null
            || examenesSeleccionados == null || examenesSeleccionados.isEmpty()) {
        model.addAttribute("error", "Debe seleccionar un paciente y al menos un examen.");
        model.addAttribute("pacientes", pacienteService.getAll());
        model.addAttribute("examenes", examenService.getAll());
        return "solicitud/agregar";
    }

    try {
        // Obtener el paciente desde la base de datos
        Paciente paciente = pacienteService.get(solicitud.getPaciente().getIdPaciente());
        if (paciente == null) {
            model.addAttribute("error", "Paciente no encontrado.");
            model.addAttribute("pacientes", pacienteService.getAll());
            model.addAttribute("examenes", examenService.getAll());
            return "solicitud/agregar";
        }
        solicitud.setPaciente(paciente);

        // Asignar usuario y fecha actual
        solicitud.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));
        solicitud.setFechaSolicitud(LocalDateTime.now());

        // Guardar solicitud con detalles
        solicitudService.guardarConDetalles(solicitud, examenesSeleccionados);

        // Mensaje de éxito
        redirectAttrs.addFlashAttribute("mensaje", "Registro realizado correctamente.");
        redirectAttrs.addFlashAttribute("clase", "success");

    } catch (Exception e) {
        e.printStackTrace(); // Opcional: para depuración
        redirectAttrs.addFlashAttribute("mensaje",
                "No se pudo proceder con el registro, intente más tarde o contacte a soporte técnico.");
        redirectAttrs.addFlashAttribute("clase", "danger");
    }

    return "redirect:/solicitud/solicitudes";
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
