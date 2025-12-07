/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Cita;
import com.laboratorio.model.CitaCalendarioDTO;
import com.laboratorio.model.DetalleCitaResponse;
import com.laboratorio.model.DetallePaquete;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Paciente;
import com.laboratorio.model.Paquete;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import com.laboratorio.service.CitaService;
import com.laboratorio.service.ExamenService;
import com.laboratorio.service.InventarioService;
import com.laboratorio.service.PacienteService;
import com.laboratorio.service.PaqueteService;
import com.laboratorio.service.SolicitudService;
import com.laboratorio.service.UsuarioService;
import com.laboratorio.services.impl.EmailServiceImpl;
import com.laboratorio.services.impl.WhatsAppServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private EmailServiceImpl emailServiceImpl;
    private final InventarioService inventarioService;
    private WhatsAppServiceImpl whatsAppServiceImpl;

    @Autowired
    public CitaController(
            CitaService citaService,
            SolicitudService solicitudService,
            UsuarioService usuarioService,
            ExamenService examenService,
            PacienteService pacienteService,
            PaqueteService paqueteService,
            EmailServiceImpl emailServiceImpl,
            InventarioService inventarioService,
            WhatsAppServiceImpl whatsAppServiceImpl
    ) {
        this.citaService = citaService;
        this.solicitudService = solicitudService;
        this.usuarioService = usuarioService;
        this.examenService = examenService;
        this.pacienteService = pacienteService;
        this.paqueteService = paqueteService;
        this.emailServiceImpl = emailServiceImpl;
        this.inventarioService = inventarioService;
        this.whatsAppServiceImpl = whatsAppServiceImpl;
    }

    // Listado de citas
    @GetMapping("/citas")
    public String listarCitas(Model model) {
        List<CitaCalendarioDTO> citasDTO = citaService.getAll().stream()
                // Filtrar solo citas activas
                .filter(c -> !"CANCELADA".equals(c.getEstado()))
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

    // Formulario para agregar una nueva cita
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
            // 1. Crear la solicitud
            Solicitud solicitud = new Solicitud();
            solicitud.setPaciente(pacienteService.get(idPaciente));
            solicitud.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));
            solicitud.setFechaSolicitud(LocalDateTime.now());
            solicitud.setEstado("Agendada");

            double precioTotal = 0.0;

            // 2. Agregar exámenes individuales a la solicitud
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

                    Paquete paquete = paqueteService.getById(idPaquete);

                    if (paquete != null) {

                        // ---------- GUARDAR EL PAQUETE COMO DETALLE -----------
                        SolicitudDetalle detallePaquete = new SolicitudDetalle();
                        detallePaquete.setPaquete(paquete);  // ← AQUÍ SÍ EXISTE "paquete"
                        detallePaquete.setExamen(null);

                        solicitud.addDetalle(detallePaquete);
                        // SUMAR PRECIO DEL PAQUETE
                        precioTotal += paquete.getPrecio().doubleValue();
                        //---------- GUARDAR LOS EXÁMENES QUE INCLUYE EL PAQUETE -----------
                        for (DetallePaquete dp : paquete.getDetalles()) {
                            Examen examenPaquete = dp.getExamen();

                            SolicitudDetalle detalleExamen = new SolicitudDetalle();
                            detalleExamen.setExamen(examenPaquete);
                            detalleExamen.setPaquete(paquete); // opcional
                            solicitud.addDetalle(detalleExamen);

                            precioTotal += examenPaquete.getPrecio().doubleValue();
                        }
                    }
                }
            }

            // Asignar precio total a la solicitud
            solicitud.setPrecioTotal(precioTotal);

            // Crear la cita asociada
            Cita cita = new Cita();
            cita.setSolicitud(solicitud);
            cita.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));
            cita.setFechaCita(fechaCita);
            cita.setNotas(notas);
            cita.setEstado("AGENDADA");

            // Guardar solicitud y cita
            solicitudService.save(solicitud);
            citaService.save(cita);
            System.out.println(cita.getIdCita());
            inventarioService.ajustarInventarioPorCita(cita.getIdCita(), cita.getEstado());
            // Enviar correo al paciente
            Paciente paciente = solicitud.getPaciente();

            String destinatario = paciente.getEmail();
            String asunto = "Confirmación de cita - Laboratorio Clínico";

            // Construir tabla HTML de exámenes
            StringBuilder examenesHtml = new StringBuilder();
            if (solicitud.getDetalles() != null && !solicitud.getDetalles().isEmpty()) {
                examenesHtml.append("""
                <table border="1" cellspacing="0" cellpadding="8" style="border-collapse: collapse; width: 100%;">
                    <thead style="background-color: #f2f2f2;">
                        <tr>
                            <th style="text-align:left;">Código</th>
                            <th style="text-align:left;">Nombre del Examen</th>
                            <th style="text-align:right;">Precio</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

                for (SolicitudDetalle detalle : solicitud.getDetalles()) {

                    // Solo procesar detalles que SÍ tienen examen
                    if (detalle.getExamen() == null) {
                        continue;
                    }

                    Examen ex = detalle.getExamen();

                    String condiciones = "";
                    if (ex.getCondiciones() != null && !ex.getCondiciones().isBlank()) {
                        condiciones = "<br><small><i>Condiciones: " + ex.getCondiciones() + "</i></small>";
                    }

                    examenesHtml.append(String.format("""
                <tr>
                    <td>%s</td>
                    <td>%s%s</td>
                    <td style="text-align:right;">₡%.2f</td>
                </tr>
            """, ex.getCodigo(), ex.getNombre(), condiciones, ex.getPrecio().doubleValue()));
                }

                examenesHtml.append("""
                </tbody>
            </table>
        """);

            } else {
                examenesHtml.append("<p><i>No se registraron exámenes en esta cita.</i></p>");
            }
            // Construir tabla HTML de paquetes
            StringBuilder paquetesHtml = new StringBuilder();

            List<Paquete> paquetesUnicos = new ArrayList<>();

            for (SolicitudDetalle detalle : solicitud.getDetalles()) {
                if (detalle.getPaquete() != null && !paquetesUnicos.contains(detalle.getPaquete())) {
                    paquetesUnicos.add(detalle.getPaquete());
                }
            }

            if (!paquetesUnicos.isEmpty()) {
                paquetesHtml.append("""
                <br>
                <h3>📦 Paquetes incluidos:</h3>
                <table border="1" cellspacing="0" cellpadding="8" style="border-collapse: collapse; width: 100%;">
                    <thead style="background-color: #f2f2f2;">
                        <tr>
                            <th style="text-align:left;">Código</th>
                            <th style="text-align:left;">Paquete</th>
                            <th style="text-align:right;">Precio</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

                for (Paquete paq : paquetesUnicos) {
                    paquetesHtml.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td style="text-align:right;">₡%.2f</td>
                    </tr>
                """, paq.getCodigo(), paq.getNombre(), paq.getPrecio().doubleValue()));
                }

                paquetesHtml.append("""
                    </tbody>
                </table>
            """);
            }

            // Formatear fecha de cita
            String fechaFormateada = cita.getFechaCita()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));

            //  Contenido HTML del correo
            String contenido = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Estimado/a %s %s %s,</h2>
            <p>Su cita ha sido registrada correctamente en nuestro sistema.</p>

            <h3>📅 Detalles de la cita:</h3>
            <ul>
                <li><b>Fecha y hora:</b> %s</li>
                <li><b>Estado:</b> %s</li>
                <li><b>Notas:</b> %s</li>
                <li><b>Precio total:</b> ₡%.2f</li>
            </ul>

            <h3>🧪 Exámenes incluidos:</h3>
            %s
            %s

            <br/>
            <p>Gracias por confiar en <b>Laboratorio Clínico</b>.</p>
            <hr/>
            <p style="font-size: 12px; color: #777;">Este es un mensaje automático, por favor no responder.</p>
        </body>
        </html>
        """.formatted(
                    paciente.getNombre(),
                    paciente.getPrimerApellido(),
                    paciente.getSegundoApellido(),
                    fechaFormateada,
                    cita.getEstado(),
                    (cita.getNotas() != null && !cita.getNotas().isEmpty()) ? cita.getNotas() : "Ninguna",
                    solicitud.getPrecioTotal(),
                    examenesHtml.toString(),
                    paquetesHtml.toString()
            );

            //Enviar correo
            emailServiceImpl.enviarCorreoCita(destinatario, asunto, contenido);
            redirectAttrs.addFlashAttribute("mensaje", "Cita registrada correctamente.");
            redirectAttrs.addFlashAttribute("clase", "success");

            // Enviar MSJ   
            try {
                String numeroPaciente = paciente.getTelefono()
                        .replaceAll("[^0-9]", "");
                if (!numeroPaciente.startsWith("506")) {
                    numeroPaciente = "506" + numeroPaciente;
                }
                StringBuilder mensajeWhatsapp = new StringBuilder();
                mensajeWhatsapp.append("Estimado/a ").append(paciente.getNombre())
                        .append(" ").append(paciente.getPrimerApellido()).append(" ").append(paciente.getSegundoApellido()).append(",\n\n")
                        .append("Su cita ha sido registrada correctamente.\n\n")
                        .append("📅 Detalles de la cita:\n")
                        .append("- Fecha y hora: ").append(fechaFormateada).append("\n")
                        .append("- Estado: ").append(cita.getEstado()).append("\n")
                        .append("- Notas: ").append(cita.getNotas() != null ? cita.getNotas() : "Ninguna").append("\n")
                        .append("- Precio total: ₡").append(String.format("%.2f", solicitud.getPrecioTotal())).append("\n\n")
                        .append("🧪 Exámenes y paquetes incluidos:\n");
                // Listar paquetes
                Set<Long> paquetesAgregados = new HashSet<>();

                for (SolicitudDetalle detalle : solicitud.getDetalles()) {

                    if (detalle.getPaquete() != null && !paquetesAgregados.contains(detalle.getPaquete().getIdPaquete())) {

                        Paquete paq = detalle.getPaquete();

                        mensajeWhatsapp.append("\n📦 Paquete: ")
                                .append(paq.getNombre())
                                .append(" (₡")
                                .append(String.format("%.2f", paq.getPrecio()))
                                .append(")\n");

                        paquetesAgregados.add(paq.getIdPaquete());
                    }

                    // Listar exámenes
                    if (detalle.getExamen() != null) {
                        Examen ex = detalle.getExamen();
                        mensajeWhatsapp.append("- ").append(ex.getNombre()).append("\n");

                        if (ex.getCondiciones() != null && !ex.getCondiciones().isBlank()) {
                            mensajeWhatsapp.append("   Condiciones: ").append(ex.getCondiciones()).append("\n");
                        }
                    }
                }

                mensajeWhatsapp.append("\nGracias por confiar en Laboratorio Clínico.");

                whatsAppServiceImpl.enviarMensaje(numeroPaciente, mensajeWhatsapp.toString());
            } catch (Exception e) {
                e.printStackTrace();
                // No interrumpir el flujo si falla WhatsApp
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("mensaje", "Error al registrar la cita.");
            redirectAttrs.addFlashAttribute("clase", "danger");
        }

        return "redirect:/cita/citas";

    }

    @GetMapping("/modificar/{id}")
    public String modificarCita(@PathVariable("id") Long idCita, Model model) {
        Cita cita = citaService.getById(idCita);
        if (cita == null) {
            return "redirect:/cita/citas";
        }

        // Obtener los IDs de exámenes y paquetes ya seleccionados
        List<Long> examenesSeleccionados = cita.getSolicitud().getDetalles().stream()
                .filter(d -> d.getExamen() != null)
                .map(d -> d.getExamen().getIdExamen())
                .toList();

        List<Long> paquetesSeleccionados = cita.getSolicitud().getDetalles().stream()
                .filter(d -> d.getPaquete() != null)
                .map(d -> d.getPaquete().getIdPaquete())
                .toList();

        model.addAttribute("cita", cita);
        model.addAttribute("examenesSeleccionados", examenesSeleccionados);
        model.addAttribute("paquetesSeleccionados", paquetesSeleccionados);
        model.addAttribute("examenesDisponibles", examenService.getAll());
        model.addAttribute("paquetesDisponibles", paqueteService.getAll());

        return "cita/modificar";
    }

    @PostMapping("/actualizar")
    public String actualizarCita(
            @RequestParam("idCita") Long idCita,
            @RequestParam("fechaCita") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaCita,
            @RequestParam(value = "estado") String estado,
            @RequestParam(value = "notas", required = false) String notas,
            @RequestParam(value = "examenesSeleccionados", required = false) List<Long> examenesSeleccionados,
            @RequestParam(value = "paquetesSeleccionados", required = false) List<Long> paquetesSeleccionados,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs) {

        try {
            // 1. Obtener la cita existente
            Cita cita = citaService.getById(idCita);
            if (cita == null) {
                redirectAttrs.addFlashAttribute("mensaje", "La cita no existe.");
                redirectAttrs.addFlashAttribute("clase", "danger");
                return "redirect:/cita/citas";
            }

            // 2. Obtener la solicitud asociada
            Solicitud solicitud = cita.getSolicitud();

            // 3. Limpiar detalles anteriores
            solicitud.getDetalles().clear();
            double precioTotal = 0.0;

            // 4. Agregar exámenes seleccionados
            if (examenesSeleccionados != null && !examenesSeleccionados.isEmpty()) {
                List<Examen> examenes = examenService.findById(examenesSeleccionados);
                for (Examen examen : examenes) {
                    SolicitudDetalle detalle = new SolicitudDetalle();
                    detalle.setExamen(examen);
                    solicitud.addDetalle(detalle);
                    precioTotal += examen.getPrecio().doubleValue();
                }
            }

            // 5. Agregar paquetes seleccionados
            if (paquetesSeleccionados != null && !paquetesSeleccionados.isEmpty()) {
                for (Long idPaquete : paquetesSeleccionados) {
                    Paquete paquete = paqueteService.getById(idPaquete);
                    if (paquete != null) {
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

            // 6. Actualizar precio total
            solicitud.setPrecioTotal(precioTotal);

            // 7. Actualizar datos de la cita
            cita.setFechaCita(fechaCita);
            cita.setEstado(estado);
            cita.setNotas(notas);
            cita.setUsuario(usuarioService.getUsuarioPorUsername(userDetails.getUsername()));

            // 8. Guardar cambios
            solicitudService.save(solicitud);
            citaService.save(cita);

            inventarioService.ajustarInventarioPorCita(idCita, estado);
            // Enviar correo al paciente
            Paciente paciente = solicitud.getPaciente();
            String destinatario = paciente.getEmail();
            String asunto;
            String contenido;

            // Formatear fecha
            String fechaFormateada = cita.getFechaCita()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));

            // Construir HTML de exámenes
            StringBuilder examenesHtml = new StringBuilder();
            if (solicitud.getDetalles() != null && !solicitud.getDetalles().isEmpty()) {
                examenesHtml.append("""
                <table border="1" cellspacing="0" cellpadding="8" 
                       style="border-collapse: collapse; width: 100%; margin-top: 10px;">
                    <thead style="background-color: #f2f2f2;">
                        <tr>
                            <th style="text-align:left;">Código</th>
                            <th style="text-align:left;">Nombre del Examen</th>
                            <th style="text-align:left;">Condiciones</th>
                            <th style="text-align:right;">Precio</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

                for (SolicitudDetalle detalle : solicitud.getDetalles()) {
                    Examen ex = detalle.getExamen();

                    examenesHtml.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td style="text-align:right;">₡%.2f</td>
                    </tr>
                """,
                            ex.getCodigo(),
                            ex.getNombre(),
                            (ex.getCondiciones() != null ? ex.getCondiciones() : "Ninguna"),
                            ex.getPrecio().doubleValue()
                    ));
                }

                examenesHtml.append("""
                    </tbody>
                </table>
            """);

            } else {
                examenesHtml.append("<p><i>No se registraron exámenes en esta cita.</i></p>");
            }

            if ("CANCELADA".equals(estado)) {
                asunto = "Cita Cancelada - Laboratorio Clínico";
                contenido = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2>Estimado/a %s %s %s,</h2>
                <p>Su cita ha sido <b style='color:red;'>cancelada</b>.</p>

                <h3>📅 Detalles de la cita</h3>
                <ul>
                    <li><b>Fecha y hora:</b> %s</li>
                    <li><b>Notas:</b> %s</li>
                    <li><b>Precio total:</b> ₡%.2f</li>
                </ul>

                <h3>🧪 Exámenes incluidos</h3>
                %s

                <p>Si desea reagendar, por favor contacte al laboratorio.</p>

                <hr/>
                <p style="font-size: 12px; color: #777;">Este es un mensaje automático, por favor no responder.</p>
            </body>
            </html>
            """.formatted(
                        paciente.getNombre(),
                        paciente.getPrimerApellido(),
                        paciente.getSegundoApellido(),
                        fechaFormateada,
                        (cita.getNotas() != null ? cita.getNotas() : "Ninguna"),
                        solicitud.getPrecioTotal(),
                        examenesHtml.toString()
                );
            } else {
                asunto = "Actualización de cita - Laboratorio Clínico";
                contenido = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2>Estimado/a %s %s %s,</h2>
                <p>Su cita ha sido actualizada correctamente.</p>

                <h3>📅 Detalles de la cita</h3>
                <ul>
                    <li><b>Fecha y hora:</b> %s</li>
                    <li><b>Estado:</b> %s</li>
                    <li><b>Notas:</b> %s</li>
                    <li><b>Precio total:</b> ₡%.2f</li>
                </ul>

                <h3>🧪 Exámenes incluidos</h3>
                %s

                <hr/>
                <p style="font-size: 12px; color: #777;">Este es un mensaje automático, por favor no responder.</p>
            </body>
            </html>
            """.formatted(
                        paciente.getNombre(),
                        paciente.getPrimerApellido(),
                        paciente.getSegundoApellido(),
                        fechaFormateada,
                        cita.getEstado(),
                        (cita.getNotas() != null ? cita.getNotas() : "Ninguna"),
                        solicitud.getPrecioTotal(),
                        examenesHtml.toString()
                );
            }

            emailServiceImpl.enviarCorreoCita(destinatario, asunto, contenido);

            //Enviar Msj whats app
            try {

                String numeroPaciente = paciente.getTelefono()
                        .replaceAll("[^0-9]", "");
                if (!numeroPaciente.startsWith("506")) {
                    numeroPaciente = "506" + numeroPaciente;
                }

                StringBuilder mensajeWhatsapp = new StringBuilder();

                // Encabezado según el estado
                if ("CANCELADA".equals(estado)) {
                    mensajeWhatsapp.append("❌ *Cita Cancelada*\n\n");
                } else {
                    mensajeWhatsapp.append("🔔 *Actualización de Cita*\n\n");
                }

                // Datos del paciente
                mensajeWhatsapp.append("Estimado/a ")
                        .append(paciente.getNombre()).append(" ")
                        .append(paciente.getPrimerApellido()).append(" ")
                        .append(paciente.getSegundoApellido()).append(",\n\n");

                // Detalles de la cita
                mensajeWhatsapp.append("📅 *Detalles de la cita:*\n")
                        .append("- Fecha y hora: ").append(fechaFormateada).append("\n")
                        .append("- Estado: ").append(cita.getEstado()).append("\n")
                        .append("- Notas: ").append(
                        cita.getNotas() != null ? cita.getNotas() : "Ninguna"
                ).append("\n")
                        .append("- Precio total: ₡").append(
                        String.format("%.2f", solicitud.getPrecioTotal())
                ).append("\n\n");

                mensajeWhatsapp.append("🧪 *Exámenes incluidos:*\n");

                // Listar exámenes igual que registrar
                if (!solicitud.getDetalles().isEmpty()) {
                    for (SolicitudDetalle detalle : solicitud.getDetalles()) {
                        if (detalle.getExamen() != null) {

                            Examen ex = detalle.getExamen();
                            mensajeWhatsapp.append("- ").append(ex.getNombre()).append("\n");

                            if (ex.getCondiciones() != null && !ex.getCondiciones().isBlank()) {
                                mensajeWhatsapp.append("   Condiciones: ").append(ex.getCondiciones()).append("\n");
                            }
                        }
                    }
                } else {
                    mensajeWhatsapp.append("No se registraron exámenes.\n");
                }

                mensajeWhatsapp.append("\nGracias por confiar en Laboratorio Clínico.");

                // Enviar mensaje
                whatsAppServiceImpl.enviarMensaje(numeroPaciente, mensajeWhatsapp.toString());

            } catch (Exception e) {
                e.printStackTrace();
                // No detener flujo si WhatsApp falla
            }

            redirectAttrs.addFlashAttribute("mensaje", "Cita actualizada correctamente.");
            redirectAttrs.addFlashAttribute("clase", "success");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("mensaje", "Error al actualizar la cita.");
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

    @GetMapping("/detalle/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalleCita(@PathVariable("id") Long idCita) {
        try {
            Cita cita = citaService.getById(idCita);
            if (cita == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cita no encontrada");
            }

            Solicitud solicitud = cita.getSolicitud();
            if (solicitud == null || solicitud.getDetalles() == null) {
                return ResponseEntity.ok(new DetalleCitaResponse());
            }

            List<DetalleCitaResponse.Item> examenes = new ArrayList<>();
            List<DetalleCitaResponse.Item> paquetes = new ArrayList<>();

            Set<Long> paquetesAgregados = new HashSet<>();

            for (SolicitudDetalle d : solicitud.getDetalles()) {

                if (d.getExamen() != null) {
                    Examen ex = d.getExamen();
                    examenes.add(new DetalleCitaResponse.Item(
                            ex.getCodigo(),
                            ex.getNombre(),
                            ex.getCondiciones()
                    ));
                }

                if (d.getPaquete() != null && paquetesAgregados.add(d.getPaquete().getIdPaquete())) {
                    Paquete paq = d.getPaquete();
                    paquetes.add(new DetalleCitaResponse.Item(
                            paq.getCodigo(),
                            paq.getNombre(),
                            null
                    ));
                }
            }

            DetalleCitaResponse resp = new DetalleCitaResponse();
            resp.setExamenes(examenes);
            resp.setPaquetes(paquetes);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener detalle");
        }
    }

}
