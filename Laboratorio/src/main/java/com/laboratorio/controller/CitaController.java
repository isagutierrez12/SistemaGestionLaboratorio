/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;
import com.laboratorio.model.Cita;
import com.laboratorio.model.CitaCalendarioDTO;
import com.laboratorio.model.DetallePaquete;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Paquete;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.service.CitaService;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.PacienteService;
import com.laboratorio.service.PaqueteService;
import com.laboratorio.service.SolicitudService;
import com.laboratorio.service.UsuarioService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author soportetecnico
 */
@Controller
@RequestMapping("/cita")

public class CitaController {
    
    private final CitaService citaService;
    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;
    private final ExamenService examenService;
    private final PacienteService pacienteService;
    private final PaqueteService paqueteService;
            
    @Autowired
    public CitaController(
            CitaService citaService,
            SolicitudService solicitudService,
            UsuarioService usuarioService,
            ExamenService examenService,
            PacienteService pacienteService,
            PaqueteService paqueteService
            ) {
        this.citaService = citaService;
        this.solicitudService = solicitudService;
        this.usuarioService = usuarioService;
        this.examenService = examenService;
        this.pacienteService = pacienteService;
        this.paqueteService = paqueteService;
    }

    // Listado de citas
    @GetMapping("/citas")
    public String listarCitas(Model model) {
       List<CitaCalendarioDTO> citasDTO = citaService.getAll().stream()
    .map(c -> {
        var solicitud = c.getSolicitud();

        // Extraer nombres de exámenes y paquetes
        List<String> examenes = solicitud.getDetalles().stream()
            .filter(d -> d.getExamen() != null)
            .map(d -> d.getExamen().getNombre())
            .toList();

        List<String> paquetes = solicitud.getDetalles().stream()
            .filter(d -> d.getPaquete() != null)
            .map(d -> d.getPaquete().getNombre())
            .toList();

        return new CitaCalendarioDTO(
            c.getIdCita(),
            solicitud.getPaciente().getNombre() + " " + solicitud.getPaciente().getPrimerApellido(),
            c.getFechaCita(),
            c.getEstado(),
            c.getNotas(),
            examenes,
            paquetes
        );
    })
    .toList();

    model.addAttribute("citas", citasDTO);
    return "cita/citas";
    }

    // Agregar Cita
    @GetMapping("/agregar")
    public String agregarCita(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("pacientes", pacienteService.getAll());
        model.addAttribute("examenesDisponibles", examenService.getAll());
        model.addAttribute("paquetesDisponibles", paqueteService.getAll());
        model.addAttribute("solicitudes", solicitudService.getAll());
        return "cita/agregar";
    }

    // Guardar cita
    @PostMapping("/guardar")
    public String guardarCita(
        @RequestParam("idPaciente") String idPaciente,
        @RequestParam("fechaCita") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCita,
        @RequestParam(value = "notas", required = false) String notas,
        @RequestParam(value = "examenesSeleccionados", required = false) List<Long> examenesSeleccionados,
        @RequestParam(value = "paquetesSeleccionados", required = false) List<Long> paquetesSeleccionados,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttrs) {

    try {
        //Crear la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setPaciente(pacienteService.get(idPaciente));
        solicitud.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado("Pendiente");

        double precioTotal = 0.0;

        // Agregar exámenes individuales a la solicitud
        if (examenesSeleccionados != null && !examenesSeleccionados.isEmpty()) {
            List<Examen> examenes = examenService.findById(examenesSeleccionados);
            for (Examen examen : examenes) {
                SolicitudDetalle detalle = new SolicitudDetalle();
                detalle.setExamen(examen);
                solicitud.addDetalle(detalle);
                precioTotal += examen.getPrecio().doubleValue();
            }
        }

        // 3. Agregar paquetes (y sus exámenes) a la solicitud
      if (paquetesSeleccionados != null && !paquetesSeleccionados.isEmpty()) {
        for (Long idPaquete : paquetesSeleccionados) {
            Paquete paquete = paqueteService.getById(idPaquete); // ✅ Correcto
            if (paquete != null) {
                // Agregar los exámenes del paquete a la solicitud
                for (DetallePaquete dp : paquete.getDetalles()) {
                    Examen examenPaquete = dp.getExamen();
                    SolicitudDetalle detalle = new SolicitudDetalle();
                    detalle.setExamen(examenPaquete);
                    solicitud.addDetalle(detalle);
                    precioTotal += examenPaquete.getPrecio().doubleValue();
                }
            }
        }
    }

        // 4. Asignar precio total a la solicitud
        solicitud.setPrecioTotal(precioTotal);

        // 5. Crear la cita asociada
        Cita cita = new Cita();
        cita.setSolicitud(solicitud);
        cita.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));
        cita.setFechaCita(fechaCita);
        cita.setNotas(notas);
        cita.setEstado("Pendiente");

        // 6. Guardar solicitud y cita
        solicitudService.save(solicitud);
        citaService.save(cita);

        redirectAttrs.addFlashAttribute("mensaje", "Cita registrada correctamente.");
        redirectAttrs.addFlashAttribute("clase", "success");

    } catch (Exception e) {
        e.printStackTrace();
        redirectAttrs.addFlashAttribute("mensaje", "Error al registrar la cita.");
        redirectAttrs.addFlashAttribute("clase", "danger");
    }

    return "redirect:/cita/citas";

    }

    // Buscar cita por solicitud o estado
    @GetMapping("/buscar")
    public String buscarCitas(@RequestParam("query") String query, Model model) {
        List<Cita> citas;
        if (query == null || query.trim().isEmpty()) {
            citas = citaService.getAll();
        } else {
            // Puedes luego implementar un método personalizado en el servicio/repo
            citas = citaService.getAll().stream()
                    .filter(c -> c.getEstado() != null && c.getEstado().toLowerCase().contains(query.toLowerCase()))
                    .toList();
        }
        model.addAttribute("citas", citas);
        model.addAttribute("query", query);
        return "cita/citas";
    }
}