package com.laboratorio;

import com.laboratorio.model.Auditoria;
import com.laboratorio.model.Cita;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Insumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.model.Paciente;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.CitaRepository;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.repository.InsumoRepository;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.PacienteRepository;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.AuditoriaService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Aspect
@Component
public class AuditoriaAspect {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InsumoRepository InsumoRepository;

    @Autowired(required = false)
    private HttpServletRequest request;

    private final Map<String, Boolean> estadoUsuarioPrevio = new ConcurrentHashMap<>();

    // ===== USUARIO =====
    private final Map<String, Usuario> usuarioPrevio = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.save(com.laboratorio.model.Usuario,..))")

    public void antesDeGuardarUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {
            boolean esNuevo = (usuario.getIdUsuario() == null)
                    || !usuarioRepository.existsById(usuario.getIdUsuario());

            estadoUsuarioPrevio.put(Thread.currentThread().getName(), esNuevo);

            if (!esNuevo) {
                Usuario anterior = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
                if (anterior != null) {
                    Usuario clon = new Usuario();
                    BeanUtils.copyProperties(anterior, clon);
                    usuarioPrevio.put(Thread.currentThread().getName(), clon);
                }
            }
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.save(com.laboratorio.model.Usuario,..))")

    public void registrarGuardarUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {

            boolean esNuevo = estadoUsuarioPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoUsuarioPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";

            StringBuilder cambios = new StringBuilder();

            if (!esNuevo) {
                Usuario viejo = usuarioPrevio.get(Thread.currentThread().getName());
                usuarioPrevio.remove(Thread.currentThread().getName());

                if (viejo != null) {
                    detectarCambiosUsuario(viejo, usuario, cambios);
                }
            }
            registrarAuditoria(usuario, accion, cambios.toString());
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.delete(..))")
    public void auditarEliminacionUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {
            registrarAuditoria(usuario, "ELIMINAR", "");
        }
    }

    // ===== PACIENTE =====
    @Autowired
    private PacienteRepository pacienteRepository;

    private final Map<String, Paciente> pacientePrevio = new ConcurrentHashMap<>();
    private final Map<String, Boolean> estadoPrevioPaciente = new ConcurrentHashMap<>();

    @Autowired
    private EntityManager entityManager;

    @Before("execution(* com.laboratorio.services.impl.PacienteServiceImpl.save(com.laboratorio.model.Paciente,..))")
    public void antesDeGuardarPaciente(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Paciente pacienteModificado) {
                String threadKey = Thread.currentThread().getName();

                boolean esNuevo = (pacienteModificado.getIdPaciente() == null);

                estadoPrevioPaciente.put(threadKey, esNuevo);

                if (!esNuevo) {
                    entityManager.clear();
                    Paciente original = pacienteRepository.findById(pacienteModificado.getIdPaciente())
                            .orElse(null);

                    if (original != null) {
                        Paciente clon = clonarPaciente(original);
                        pacientePrevio.put(threadKey, clon);

                    }
                } else {
                    log.info("Nuevo paciente - No hay estado previo");
                }
            }
        } catch (Exception e) {
            log.error("Error en antesDeGuardarPaciente", e);
        }
    }

    private Paciente clonarPaciente(Paciente original) {
        if (original == null) {
            return null;
        }

        Paciente clon = new Paciente();

        // Copiar manualmente todos los campos
        clon.setIdPaciente(original.getIdPaciente());
        clon.setNombre(original.getNombre());
        clon.setPrimerApellido(original.getPrimerApellido());
        clon.setSegundoApellido(original.getSegundoApellido());
        clon.setCedula(original.getCedula());
        clon.setFechaNacimiento(original.getFechaNacimiento());
        clon.setTelefono(original.getTelefono());
        clon.setEmail(original.getEmail());
        clon.setContactoEmergencia(original.getContactoEmergencia());
        clon.setPadecimiento(original.getPadecimiento());
        clon.setAlergia(original.getAlergia());
        clon.setActivo(original.getActivo());
        clon.setFechaCreacion(original.getFechaCreacion());

        return clon;
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PacienteServiceImpl.save(com.laboratorio.model.Paciente,..))")
    public void registrarGuardarPaciente(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Paciente paciente) {

            boolean esNuevo = estadoPrevioPaciente.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevioPaciente.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";

            StringBuilder cambios = new StringBuilder();

            if (!esNuevo) {
                Paciente viejo = pacientePrevio.get(Thread.currentThread().getName());
                pacientePrevio.remove(Thread.currentThread().getName());

                if (viejo != null) {
                    detectarCambiosPaciente(viejo, paciente, cambios);
                }
            }

            registrarAuditoria(paciente, accion, cambios.toString());
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PacienteServiceImpl.delete(..))")
    public void auditarEliminacionPaciente(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paciente paciente) {
            registrarAuditoria(paciente, "ELIMINAR", "");
        }
    }

    // ===== EXAMEN =====
    @Autowired
    private ExamenRepository examenRepository;

    private final Map<String, Examen> examenPrevio = new ConcurrentHashMap<>();
    private final Map<String, Boolean> estadoPrevioExamen = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.ExamenServiceImpl.save(com.laboratorio.model.Examen,..))")
    public void antesDeGuardarExamen(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Examen examenModificado) {

                String threadKey = Thread.currentThread().getName();
                boolean esNuevo = (examenModificado.getIdExamen() == null);

                estadoPrevioExamen.put(threadKey, esNuevo);

                if (!esNuevo) {
                    entityManager.clear();
                    Examen original = examenRepository.findById(examenModificado.getIdExamen())
                            .orElse(null);

                    if (original != null) {
                        Examen clon = clonarExamen(original);
                        examenPrevio.put(threadKey, clon);
                    }
                } else {
                    log.info("Nuevo examen - No hay estado previo");
                }
            }
        } catch (Exception e) {
            log.error("Error en antesDeGuardarExamen", e);
        }
    }

    private Examen clonarExamen(Examen original) {
        if (original == null) {
            return null;
        }

        Examen clon = new Examen();

        clon.setIdExamen(original.getIdExamen());
        clon.setCodigo(original.getCodigo());
        clon.setNombre(original.getNombre());
        clon.setArea(original.getArea());
        clon.setPrecio(original.getPrecio());
        clon.setCondiciones(original.getCondiciones());
        clon.setUnidad(original.getUnidad());
        clon.setValorMinimo(original.getValorMinimo());
        clon.setValorMaximo(original.getValorMaximo());
        clon.setActivo(original.getActivo());

        return clon;
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.ExamenServiceImpl.save(com.laboratorio.model.Examen,..))")
    public void registrarGuardarExamen(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Examen examen) {

            String threadKey = Thread.currentThread().getName();
            boolean esNuevo = estadoPrevioExamen.getOrDefault(threadKey, false);
            estadoPrevioExamen.remove(threadKey);

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";

            StringBuilder cambios = new StringBuilder();

            if (!esNuevo) {
                Examen viejo = examenPrevio.get(threadKey);
                examenPrevio.remove(threadKey);

                if (viejo != null) {
                    detectarCambiosExamen(viejo, examen, cambios);
                }
            }

            registrarAuditoria(examen, accion, cambios.toString());
        }
    }

    // ===== INVENTARIO =====
    @Autowired
    private InventarioRepository inventarioRepository;

    private final Map<String, Inventario> inventarioPrevio = new ConcurrentHashMap<>();
    private final Map<String, Boolean> estadoPrevioInventario = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.InventarioServiceImpl.save(com.laboratorio.model.Inventario,..))")
    public void antesDeGuardarInventario(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Inventario invModificado) {

                String key = Thread.currentThread().getName();

                boolean esNuevo = (invModificado.getIdInventario() == null);
                estadoPrevioInventario.put(key, esNuevo);

                if (!esNuevo) {

                    entityManager.clear();

                    Inventario original = inventarioRepository.findById(invModificado.getIdInventario())
                            .orElse(null);

                    if (original != null) {
                        Inventario clon = clonarInventario(original);
                        inventarioPrevio.put(key, clon);
                    }

                } else {
                    log.info("Nuevo inventario - No hay estado previo");
                }
            }
        } catch (Exception e) {
            log.error("Error en antesDeGuardarInventario", e);
        }
    }

    private Inventario clonarInventario(Inventario o) {

        Inventario c = new Inventario();

        c.setIdInventario(o.getIdInventario());
        c.setInsumo(o.getInsumo());
        c.setCodigoBarras(o.getCodigoBarras());
        c.setStockActual(o.getStockActual());
        c.setStockBloqueado(o.getStockBloqueado());
        c.setStockMinimo(o.getStockMinimo());
        c.setFechaApertura(o.getFechaApertura());
        c.setFechaVencimiento(o.getFechaVencimiento());
        c.setActivo(o.getActivo());

        return c;
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InventarioServiceImpl.save(com.laboratorio.model.Inventario,..))")
    public void registrarGuardarInventario(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Inventario inventario)) {
            return;
        }

        String key = Thread.currentThread().getName();

        boolean esNuevo = estadoPrevioInventario.getOrDefault(key, false);
        estadoPrevioInventario.remove(key);

        String accion = esNuevo ? "CREAR" : "ACTUALIZAR";

        StringBuilder cambios = new StringBuilder();

        if (!esNuevo) {
            Inventario viejo = inventarioPrevio.get(key);
            inventarioPrevio.remove(key);

            if (viejo != null) {
                detectarCambiosInventario(viejo, inventario, cambios);
            }
        }

        registrarAuditoria(inventario, accion, cambios.toString());
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InventarioServiceImpl.delete(..))")
    public void auditarEliminacionInventario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Inventario inventario) {
            registrarAuditoria(inventario, "ELIMINAR", "");
        }
    }

    // ===== INSUMO =====
    @Autowired
    private InsumoRepository insumoRepository;

    private final Map<String, Insumo> insumoPrevio = new ConcurrentHashMap<>();
    private final Map<String, Boolean> estadoPrevioInsumo = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.InsumoServiceImpl.save(com.laboratorio.model.Insumo,..))")
    public void antesDeGuardarInsumo(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Insumo insumoModificado) {

                String key = Thread.currentThread().getName();
                boolean esNuevo = (insumoModificado.getIdInsumo() == null);

                estadoPrevioInsumo.put(key, esNuevo);

                if (!esNuevo) {

                    entityManager.clear();

                    Insumo original = insumoRepository.findById(insumoModificado.getIdInsumo())
                            .orElse(null);

                    if (original != null) {
                        Insumo clon = clonarInsumo(original);
                        insumoPrevio.put(key, clon);
                    }

                } else {
                    log.info("Nuevo insumo - No hay estado previo");
                }
            }
        } catch (Exception e) {
            log.error("Error en antesDeGuardarInsumo", e);
        }
    }

    private Insumo clonarInsumo(Insumo o) {

        Insumo c = new Insumo();

        c.setIdInsumo(o.getIdInsumo());
        c.setNombre(o.getNombre());
        c.setTipo(o.getTipo());
        c.setCantidadPorUnidad(o.getCantidadPorUnidad());
        c.setUnidadMedida(o.getUnidadMedida());
        c.setActivo(o.isActivo());

        return c;
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InsumoServiceImpl.save(com.laboratorio.model.Insumo,..))")
    public void registrarGuardarInsumo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || !(args[0] instanceof Insumo insumo)) {
            return;
        }

        String key = Thread.currentThread().getName();

        boolean esNuevo = estadoPrevioInsumo.getOrDefault(key, false);
        estadoPrevioInsumo.remove(key);

        String accion = esNuevo ? "CREAR" : "ACTUALIZAR";

        StringBuilder cambios = new StringBuilder();

        if (!esNuevo) {
            Insumo viejo = insumoPrevio.get(key);
            insumoPrevio.remove(key);

            if (viejo != null) {
                detectarCambiosInsumo(viejo, insumo, cambios);
            }
        }

        registrarAuditoria(insumo, accion, cambios.toString());
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InsumoServiceImpl.delete(..))")
    public void auditarEliminacionInsumo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Insumo insumo) {
            registrarAuditoria(insumo, "ELIMINAR", "");
        }
    }

    // ===== CITA =====
    @Autowired
    private CitaRepository citaRepository;

    private final Map<String, Cita> citaPrevio = new ConcurrentHashMap<>();
    private final Map<String, Boolean> estadoPrevioCita = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.CitaServiceImpl.save(com.laboratorio.model.Cita,..))")
    public void antesDeGuardarCita(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();

            if (args.length > 0 && args[0] instanceof Cita citaModificada) {

                String threadKey = Thread.currentThread().getName();
                boolean esNuevo = (citaModificada.getIdCita() == null);
                estadoPrevioCita.put(threadKey, esNuevo);

                if (!esNuevo) {

                    entityManager.clear();

                    Cita original = citaRepository.findById(citaModificada.getIdCita())
                            .orElse(null);

                    if (original != null) {
                        if (original.getSolicitud() != null) {
                            Hibernate.initialize(original.getSolicitud());
                            Hibernate.initialize(original.getSolicitud().getPaciente());
                        }
                        if (original.getUsuario() != null) {
                            Hibernate.initialize(original.getUsuario());
                        }

                        Cita clon = clonarCita(original);
                        citaPrevio.put(threadKey, clon);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error en antesDeGuardarCita", e);
        }
    }

    private Cita clonarCita(Cita original) {
        if (original == null) {
            return null;
        }

        Cita clon = new Cita();

        clon.setIdCita(original.getIdCita());
        clon.setFechaCita(original.getFechaCita());
        clon.setNotas(original.getNotas());
        clon.setEstado(original.getEstado());

        // Relaciones
        clon.setSolicitud(original.getSolicitud());
        clon.setUsuario(original.getUsuario());

        return clon;
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.CitaServiceImpl.save(com.laboratorio.model.Cita,..))")
    public void registrarGuardarCita(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof Cita cita) {

            String threadKey = Thread.currentThread().getName();
            boolean esNuevo = estadoPrevioCita.getOrDefault(threadKey, false);
            estadoPrevioCita.remove(threadKey);

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            StringBuilder cambios = new StringBuilder();

            if (!esNuevo) {
                Cita vieja = citaPrevio.get(threadKey);
                citaPrevio.remove(threadKey);

                if (vieja != null) {
                    detectarCambiosCita(vieja, cita, cambios);
                }
            }

            registrarAuditoria(cita, accion, cambios.toString());
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.CitaServiceImpl.delete(..))")
    public void auditarEliminacionCita(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Cita cita) {
            registrarAuditoria(cita, "ELIMINAR", "");
        }
    }

    // ===== MÉTODO GENERAL DE REGISTRO =====
    private void registrarAuditoria(Object entidadObjeto, String accion, String cambios) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setFechaHora(new Date());

            String entidad = entidadObjeto.getClass().getSimpleName();
            auditoria.setModulo(entidad.toUpperCase());
            auditoria.setEntidadAfectada(entidad);

            auditoria.setAccion(accion);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null) ? auth.getName() : "Desconocido";
            auditoria.setUsuario(username);

            auditoria.setIpOrigen(request != null ? request.getRemoteAddr() : "127.0.0.1");

            if (entidadObjeto instanceof Paciente paciente) {

                auditoria.setIdEntidad(String.valueOf(paciente.getIdPaciente()));

                if (accion.equals("CREAR")) {

                    auditoria.setDescripcion(
                            "Se registró un nuevo paciente con cédula " + paciente.getCedula()
                    );

                    auditoria.setDatosAdicionales(
                            "Paciente afectado: "
                            + paciente.getNombre() + " "
                            + paciente.getPrimerApellido()
                    );

                } else {

                    auditoria.setDescripcion(
                            "Paciente afectado: "
                            + paciente.getNombre() + " "
                            + paciente.getPrimerApellido()
                    );

                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }

            } else if (entidadObjeto instanceof Usuario usuario) {
                auditoria.setIdEntidad(String.valueOf(usuario.getIdUsuario()));

                if (accion.equals("CREAR")) {
                    auditoria.setDescripcion(
                            "Se registró un nuevo usuario con cédula " + usuario.getCedula()
                    );

                    auditoria.setDatosAdicionales(
                            "Usuario afectado: " + usuario.getNombre() + " " + usuario.getPrimerApellido()
                    );

                } else {
                    auditoria.setDescripcion(
                            "Usuario afectado: " + usuario.getNombre() + " " + usuario.getPrimerApellido()
                    );

                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }

            } else if (entidadObjeto instanceof Examen examen) {

                auditoria.setIdEntidad(String.valueOf(examen.getIdExamen()));

                if (accion.equals("CREAR")) {
                    auditoria.setDescripcion(
                            "Se registró un nuevo examen: " + examen.getNombre()
                    );
                    auditoria.setDatosAdicionales(
                            "Área: " + examen.getArea() + ", Precio: " + examen.getPrecio()
                    );
                } else {
                    auditoria.setDescripcion(
                            "Examen afectado: " + examen.getNombre()
                    );
                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }
            } else if (entidadObjeto instanceof Inventario inv) {

                auditoria.setIdEntidad(String.valueOf(inv.getIdInventario()));

                Insumo insumo = inv.getInsumo();

                if (insumo != null && insumo.getNombre() == null) {
                    insumo = insumoRepository.findById(insumo.getIdInsumo())
                            .orElse(insumo);
                }

                String nombreInsumo = (insumo != null ? insumo.getNombre() : "Sin nombre");

                if (accion.equals("CREAR")) {

                    auditoria.setDescripcion(
                            "Se registró un nuevo inventario para el insumo " + nombreInsumo
                    );

                    auditoria.setDatosAdicionales(
                            "Código de barras: " + inv.getCodigoBarras()
                    );

                } else {

                    auditoria.setDescripcion(
                            "Inventario afectado para insumo: " + nombreInsumo
                    );

                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }

            } else if (entidadObjeto instanceof Insumo ins) {

                auditoria.setIdEntidad(String.valueOf(ins.getIdInsumo()));

                if (accion.equals("CREAR")) {
                    auditoria.setDescripcion(
                            "Se registró un nuevo insumo: " + ins.getNombre()
                    );

                    auditoria.setDatosAdicionales(
                            "Tipo: " + ins.getTipo()
                    );

                } else {
                    auditoria.setDescripcion(
                            "Insumo afectado: " + ins.getNombre()
                    );

                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }
            } else if (entidadObjeto instanceof Cita cita) {

                auditoria.setIdEntidad(String.valueOf(cita.getIdCita()));

                Solicitud solicitud = cita.getSolicitud();
                Usuario usuario = cita.getUsuario();

                String nombrePaciente = "Paciente no disponible";
                String nombreUsuario = "Usuario no disponible";

                if (solicitud != null && solicitud.getPaciente() != null) {
                    Paciente p = solicitud.getPaciente();
                    nombrePaciente = p.getNombre() + " " + p.getPrimerApellido();
                }

                if (usuario != null) {
                    nombreUsuario = usuario.getNombre() + " " + usuario.getPrimerApellido();
                }

                if (accion.equals("CREAR")) {
                    auditoria.setDescripcion(
                            "Se creó una nueva cita para el paciente " + nombrePaciente
                    );

                    auditoria.setDatosAdicionales(
                            "Fecha: " + cita.getFechaCita()
                            + ", Asignada a: " + nombreUsuario
                    );

                } else {

                    auditoria.setDescripcion(
                            "Cita modificada para el paciente " + nombrePaciente
                    );

                    auditoria.setDatosAdicionales(
                            (cambios == null || cambios.isBlank())
                            ? "No hubo cambios."
                            : "Cambios realizados: " + cambios
                    );
                }
            } else {
                auditoria.setDescripcion("Entidad afectada: " + entidad);
            }

            auditoriaService.registrarAuditoria(auditoria);

        } catch (Exception e) {
            log.error("Error registrando auditoría automática", e);
        }
    }

    private void detectarCambiosUsuario(Usuario viejo, Usuario nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getNombre(), nuevo.getNombre())) {
            cambios.append("Nombre cambiado de '")
                    .append(viejo.getNombre())
                    .append("' a '")
                    .append(nuevo.getNombre())
                    .append("'. ");
        }

        if (!Objects.equals(viejo.getPrimerApellido(), nuevo.getPrimerApellido())) {
            cambios.append("Primer apellido cambiado de '")
                    .append(viejo.getPrimerApellido())
                    .append("' a '")
                    .append(nuevo.getPrimerApellido())
                    .append("'. ");
        }

        if (!Objects.equals(viejo.getSegundoApellido(), nuevo.getSegundoApellido())) {
            cambios.append("Segundo apellido cambiado de '")
                    .append(viejo.getSegundoApellido())
                    .append("' a '")
                    .append(nuevo.getSegundoApellido())
                    .append("'. ");
        }

        if (!Objects.equals(viejo.getUsername(), nuevo.getUsername())) {
            cambios.append("Username cambiado de '")
                    .append(viejo.getUsername())
                    .append("' a '")
                    .append(nuevo.getUsername())
                    .append("'. ");
        }

        if (!Objects.equals(viejo.getCedula(), nuevo.getCedula())) {
            cambios.append("Cédula cambiada de '")
                    .append(viejo.getCedula())
                    .append("' a '")
                    .append(nuevo.getCedula())
                    .append("'. ");
        }

        if (viejo.getActivo() != nuevo.getActivo()) {
            cambios.append("Estado activo cambiado de '")
                    .append(viejo.getActivo())
                    .append("' a '")
                    .append(nuevo.getActivo())
                    .append("'. ");
        }
    }

    // ===== DETECCIÓN DE CAMBIOS PACIENTE =====
    private void detectarCambiosPaciente(Paciente viejo, Paciente nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getNombre(), nuevo.getNombre())) {
            cambios.append("Nombre cambiado de '")
                    .append(viejo.getNombre()).append("' a '")
                    .append(nuevo.getNombre()).append("'. ");
        }

        if (!Objects.equals(viejo.getPrimerApellido(), nuevo.getPrimerApellido())) {
            cambios.append("Primer apellido cambiado de '")
                    .append(viejo.getPrimerApellido()).append("' a '")
                    .append(nuevo.getPrimerApellido()).append("'. ");
        }

        if (!Objects.equals(viejo.getSegundoApellido(), nuevo.getSegundoApellido())) {
            cambios.append("Segundo apellido cambiado de '")
                    .append(viejo.getSegundoApellido()).append("' a '")
                    .append(nuevo.getSegundoApellido()).append("'. ");
        }

        if (!Objects.equals(viejo.getCedula(), nuevo.getCedula())) {
            cambios.append("Cédula cambiada de '")
                    .append(viejo.getCedula()).append("' a '")
                    .append(nuevo.getCedula()).append("'. ");
        }

        if (!Objects.equals(viejo.getFechaNacimiento(), nuevo.getFechaNacimiento())) {
            cambios.append("Fecha nacimiento cambiada de '")
                    .append(viejo.getFechaNacimiento()).append("' a '")
                    .append(nuevo.getFechaNacimiento()).append("'. ");
        }

        if (!Objects.equals(viejo.getTelefono(), nuevo.getTelefono())) {
            cambios.append("Teléfono cambiado de '")
                    .append(viejo.getTelefono()).append("' a '")
                    .append(nuevo.getTelefono()).append("'. ");
        }

        if (!Objects.equals(viejo.getEmail(), nuevo.getEmail())) {
            cambios.append("Email cambiado de '")
                    .append(viejo.getEmail()).append("' a '")
                    .append(nuevo.getEmail()).append("'. ");
        }

        if (!Objects.equals(viejo.getContactoEmergencia(), nuevo.getContactoEmergencia())) {
            cambios.append("Contacto emergencia cambiado de '")
                    .append(viejo.getContactoEmergencia()).append("' a '")
                    .append(nuevo.getContactoEmergencia()).append("'. ");
        }

        if (!Objects.equals(viejo.getPadecimiento(), nuevo.getPadecimiento())) {
            cambios.append("Padecimiento cambiado de '")
                    .append(viejo.getPadecimiento()).append("' a '")
                    .append(nuevo.getPadecimiento()).append("'. ");
        }

        if (!Objects.equals(viejo.getAlergia(), nuevo.getAlergia())) {
            cambios.append("Alergia cambiada de '")
                    .append(viejo.getAlergia()).append("' a '")
                    .append(nuevo.getAlergia()).append("'. ");
        }

        if (!Objects.equals(viejo.getActivo(), nuevo.getActivo())) {
            cambios.append("Estado activo cambiado de '")
                    .append(viejo.getActivo()).append("' a '")
                    .append(nuevo.getActivo()).append("'. ");
        }
    }

    // ===== DETECCIÓN DE CAMBIOS EXAMEN =====
    private void detectarCambiosExamen(Examen viejo, Examen nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getNombre(), nuevo.getNombre())) {
            cambios.append("Nombre cambiado de '")
                    .append(viejo.getNombre()).append("' a '")
                    .append(nuevo.getNombre()).append("'. ");
        }

        if (!Objects.equals(viejo.getArea(), nuevo.getArea())) {
            cambios.append("Área cambiada de '")
                    .append(viejo.getArea()).append("' a '")
                    .append(nuevo.getArea()).append("'. ");
        }

        if (!Objects.equals(viejo.getPrecio(), nuevo.getPrecio())) {
            cambios.append("Precio cambiado de '")
                    .append(viejo.getPrecio()).append("' a '")
                    .append(nuevo.getPrecio()).append("'. ");
        }

        if (!Objects.equals(viejo.getCondiciones(), nuevo.getCondiciones())) {
            cambios.append("Condiciones cambiadas de '")
                    .append(viejo.getCondiciones()).append("' a '")
                    .append(nuevo.getCondiciones()).append("'. ");
        }

        if (!Objects.equals(viejo.getUnidad(), nuevo.getUnidad())) {
            cambios.append("Unidad cambiada de '")
                    .append(viejo.getUnidad()).append("' a '")
                    .append(nuevo.getUnidad()).append("'. ");
        }

        if (viejo.getValorMinimo() != nuevo.getValorMinimo()) {
            cambios.append("Valor mínimo cambiado de '")
                    .append(viejo.getValorMinimo()).append("' a '")
                    .append(nuevo.getValorMinimo()).append("'. ");
        }

        if (viejo.getValorMaximo() != nuevo.getValorMaximo()) {
            cambios.append("Valor máximo cambiado de '")
                    .append(viejo.getValorMaximo()).append("' a '")
                    .append(nuevo.getValorMaximo()).append("'. ");
        }

        if (!Objects.equals(viejo.getActivo(), nuevo.getActivo())) {
            cambios.append("Estado activo cambiado de '")
                    .append(viejo.getActivo()).append("' a '")
                    .append(nuevo.getActivo()).append("'. ");
        }
    }

    // ===== DETECCIÓN DE CAMBIOS INVENTARIO =====
    private void detectarCambiosInventario(Inventario viejo, Inventario nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getInsumo(), nuevo.getInsumo())) {
            cambios.append("Insumo cambiado de '")
                    .append(viejo.getInsumo().getNombre()).append("' a '")
                    .append(nuevo.getInsumo().getNombre()).append("'. ");
        }

        if (!Objects.equals(viejo.getCodigoBarras(), nuevo.getCodigoBarras())) {
            cambios.append("Código de barras cambiado de '")
                    .append(viejo.getCodigoBarras()).append("' a '")
                    .append(nuevo.getCodigoBarras()).append("'. ");
        }

        if (!Objects.equals(viejo.getStockActual(), nuevo.getStockActual())) {
            cambios.append("Stock Actual cambiado de ")
                    .append(viejo.getStockActual()).append(" a ")
                    .append(nuevo.getStockActual()).append(". ");
        }

        if (!Objects.equals(viejo.getStockBloqueado(), nuevo.getStockBloqueado())) {
            cambios.append("Stock Bloqueado cambiado de ")
                    .append(viejo.getStockBloqueado()).append(" a ")
                    .append(nuevo.getStockBloqueado()).append(". ");
        }

        if (!Objects.equals(viejo.getStockMinimo(), nuevo.getStockMinimo())) {
            cambios.append("Stock Mínimo cambiado de ")
                    .append(viejo.getStockMinimo()).append(" a ")
                    .append(nuevo.getStockMinimo()).append(". ");
        }

        if (!Objects.equals(viejo.getFechaVencimiento(), nuevo.getFechaVencimiento())) {
            cambios.append("Fecha Vencimiento cambiada de '")
                    .append(viejo.getFechaVencimiento()).append("' a '")
                    .append(nuevo.getFechaVencimiento()).append("'. ");
        }

        if (!Objects.equals(viejo.getFechaApertura(), nuevo.getFechaApertura())) {
            cambios.append("Fecha Apertura cambiada de '")
                    .append(viejo.getFechaApertura()).append("' a '")
                    .append(nuevo.getFechaApertura()).append("'. ");
        }

        if (!Objects.equals(viejo.getActivo(), nuevo.getActivo())) {
            cambios.append("Activo cambiado de '")
                    .append(viejo.getActivo()).append("' a '")
                    .append(nuevo.getActivo()).append("'. ");
        }
    }

    // ===== DETECCIÓN DE CAMBIOS INSUMOS =====
    private void detectarCambiosInsumo(Insumo viejo, Insumo nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getNombre(), nuevo.getNombre())) {
            cambios.append("Nombre cambiado de '")
                    .append(viejo.getNombre()).append("' a '")
                    .append(nuevo.getNombre()).append("'. ");
        }

        if (!Objects.equals(viejo.getTipo(), nuevo.getTipo())) {
            cambios.append("Tipo cambiado de '")
                    .append(viejo.getTipo()).append("' a '")
                    .append(nuevo.getTipo()).append("'. ");
        }

        if (!Objects.equals(viejo.getCantidadPorUnidad(), nuevo.getCantidadPorUnidad())) {
            cambios.append("Cantidad por unidad cambiada de ")
                    .append(viejo.getCantidadPorUnidad()).append(" a ")
                    .append(nuevo.getCantidadPorUnidad()).append(". ");
        }

        if (!Objects.equals(viejo.getUnidadMedida(), nuevo.getUnidadMedida())) {
            cambios.append("Unidad de medida cambiada de '")
                    .append(viejo.getUnidadMedida()).append("' a '")
                    .append(nuevo.getUnidadMedida()).append("'. ");
        }

        if (!Objects.equals(viejo.isActivo(), nuevo.isActivo())) {
            cambios.append("Activo cambiado de '")
                    .append(viejo.isActivo()).append("' a '")
                    .append(nuevo.isActivo()).append("'. ");
        }
    }

    // ===== DETECCIÓN DE CAMBIOS CITAS =====
    private void detectarCambiosCita(Cita viejo, Cita nuevo, StringBuilder cambios) {

        if (!Objects.equals(viejo.getFechaCita(), nuevo.getFechaCita())) {
            cambios.append("Fecha de cita cambiada de '")
                    .append(viejo.getFechaCita()).append("' a '")
                    .append(nuevo.getFechaCita()).append("'. ");
        }

        if (!Objects.equals(viejo.getNotas(), nuevo.getNotas())) {
            cambios.append("Notas cambiadas de '")
                    .append(viejo.getNotas()).append("' a '")
                    .append(nuevo.getNotas()).append("'. ");
        }

        if (!Objects.equals(viejo.getEstado(), nuevo.getEstado())) {
            cambios.append("Estado cambiado de '")
                    .append(viejo.getEstado()).append("' a '")
                    .append(nuevo.getEstado()).append("'. ");
        }

        if (!Objects.equals(
                viejo.getSolicitud() != null ? viejo.getSolicitud().getIdSolicitud() : null,
                nuevo.getSolicitud() != null ? nuevo.getSolicitud().getIdSolicitud() : null)) {

            cambios.append("Solicitud cambiada de '")
                    .append(viejo.getSolicitud() != null ? viejo.getSolicitud().getIdSolicitud() : "null")
                    .append("' a '")
                    .append(nuevo.getSolicitud() != null ? nuevo.getSolicitud().getIdSolicitud() : "null")
                    .append("'. ");
        }

        if (!Objects.equals(
                viejo.getUsuario() != null ? viejo.getUsuario().getIdUsuario() : null,
                nuevo.getUsuario() != null ? nuevo.getUsuario().getIdUsuario() : null)) {

            cambios.append("Usuario asignado cambiado de '")
                    .append(viejo.getUsuario() != null ? viejo.getUsuario().getUsername() : "null")
                    .append("' a '")
                    .append(nuevo.getUsuario() != null ? nuevo.getUsuario().getUsername() : "null")
                    .append("'. ");
        }
    }

}

/*
    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired(required = false)
    private HttpServletRequest request;

    private final Map<String, Boolean> estadoPrevio = new ConcurrentHashMap<>();

    @Before("execution(* com.laboratorio.services.impl.PacienteServiceImpl.save(..))")
    public void antesDeGuardarPaciente(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paciente paciente) {
            boolean esNuevo = (paciente.getIdPaciente() == null)
                    || !pacienteRepository.existsById(paciente.getIdPaciente());
            estadoPrevio.put(Thread.currentThread().getName(), esNuevo);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PacienteServiceImpl.save(..))")
    public void registrarGuardarPaciente(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paciente paciente) {
            boolean esNuevo = estadoPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(paciente, accion);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PacienteServiceImpl.delete(..))")
    public void auditarEliminacionPaciente(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paciente paciente) {
            registrarAuditoria(paciente, "ELIMINAR");
        }
    }

    // ===== EXAMEN =====
    @AfterReturning("execution(* com.laboratorio.services.impl.ExamenServiceImpl.save(..))")
    public void registrarGuardarExamen(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Examen examen) {
            boolean esNuevo = examen.getIdExamen() == null;
            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(examen, accion);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.ExamenServiceImpl.delete(..))")
    public void auditarEliminacionExamen(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Examen examen) {
            registrarAuditoria(examen, "ELIMINAR");
        }
    }

    // ===== USUARIO =====
    @Before("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.save(..))")
    public void antesDeGuardarUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {
            boolean esNuevo = (usuario.getIdUsuario() == null)
                    || !usuarioRepository.existsById(usuario.getIdUsuario());
            estadoPrevio.put(Thread.currentThread().getName(), esNuevo);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.save(..))")
    public void registrarGuardarUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {
            boolean esNuevo = estadoPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(usuario, accion);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.delete(..))")
    public void auditarEliminacionUsuario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Usuario usuario) {
            registrarAuditoria(usuario, "ELIMINAR");
        }
    }

    // ===== INVENTARIO =====
    @Before("execution(* com.laboratorio.services.impl.InventarioServiceImpl.save(..))")
    public void antesDeGuardarInventario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Inventario inventario) {
            boolean esNuevo = (inventario.getIdInventario() == null)
                    || !inventarioRepository.existsById(inventario.getIdInventario());
            estadoPrevio.put(Thread.currentThread().getName(), esNuevo);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InventarioServiceImpl.save(..))")
    public void registrarGuardarInventario(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Inventario inventario) {
            boolean esNuevo = estadoPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(inventario, accion);
        }
    }

    // ===== INSUMO =====
    @Autowired
    private InsumoRepository insumoRepository;

    @Before("execution(* com.laboratorio.services.impl.InsumoServiceImpl.save(..))")
    public void antesDeGuardarInsumo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Insumo insumo) {
            boolean esNuevo = (insumo.getIdInsumo() == null)
                    || !insumoRepository.existsById(insumo.getIdInsumo());
            estadoPrevio.put(Thread.currentThread().getName(), esNuevo);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InsumoServiceImpl.save(..))")
    public void registrarGuardarInsumo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Insumo insumo) {
            boolean esNuevo = estadoPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(insumo, accion);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.InsumoServiceImpl.delete(..))")
    public void auditarEliminacionInsumo(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Insumo insumo) {
            registrarAuditoria(insumo, "ELIMINAR");
        }
    }

    // ===== PAQUETE =====
    @Autowired
    private PaqueteRepository paqueteRepository;

    @Before("execution(* com.laboratorio.services.impl.PaqueteServiceImpl.save(..))")
    public void antesDeGuardarPaquete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paquete paquete) {
            boolean esNuevo = (paquete.getIdPaquete() == null)
                    || !paqueteRepository.existsById(paquete.getIdPaquete());
            estadoPrevio.put(Thread.currentThread().getName(), esNuevo);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PaqueteServiceImpl.save(..))")
    public void registrarGuardarPaquete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paquete paquete) {
            boolean esNuevo = estadoPrevio.getOrDefault(Thread.currentThread().getName(), false);
            estadoPrevio.remove(Thread.currentThread().getName());

            String accion = esNuevo ? "CREAR" : "ACTUALIZAR";
            registrarAuditoria(paquete, accion);
        }
    }

    @AfterReturning("execution(* com.laboratorio.services.impl.PaqueteServiceImpl.delete(..))")
    public void auditarEliminacionPaquete(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Paquete paquete) {
            registrarAuditoria(paquete, "ELIMINAR");
        }
    }

    // ===== MÉTODO GENERAL DE REGISTRO =====
    private void registrarAuditoria(Object entidadObjeto, String accion) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setFechaHora(new Date());

            // Modulo y entidad según clase
            String entidad = entidadObjeto.getClass().getSimpleName();
            auditoria.setModulo(entidad.toUpperCase());
            auditoria.setEntidadAfectada(entidad);

            auditoria.setAccion(accion);
            auditoria.setUsuario(
                    (request != null && request.getUserPrincipal() != null)
                    ? request.getUserPrincipal().getName()
                    : "Desconocido"
            );
            auditoria.setIpOrigen(request != null ? request.getRemoteAddr() : "127.0.0.1");

            // Descripción y datos adicionales según tipo
            if (entidadObjeto instanceof Paciente paciente) {
                auditoria.setIdEntidad(paciente.getIdPaciente());
                auditoria.setDescripcion("Paciente afectado: " + paciente.getNombre() + " " + paciente.getPrimerApellido());
                auditoria.setDatosAdicionales(paciente.toString());
            } else if (entidadObjeto instanceof Examen examen) {
                auditoria.setIdEntidad(String.valueOf(examen.getIdExamen()));
                auditoria.setDescripcion("Examen afectado: " + examen.getNombre());
                auditoria.setDatosAdicionales(examen.toString());
            } else if (entidadObjeto instanceof Usuario usuario) {
                auditoria.setIdEntidad(String.valueOf(usuario.getIdUsuario()));
                auditoria.setDescripcion("Usuario afectado: " + usuario.getNombre() + " " + usuario.getPrimerApellido());
                auditoria.setDatosAdicionales(usuario.toString());
            } else if (entidadObjeto instanceof Insumo insumo) {
                auditoria.setIdEntidad(String.valueOf(insumo.getIdInsumo()));
                auditoria.setDescripcion("Insumo afectado: " + insumo.getNombre());
                auditoria.setDatosAdicionales(insumo.toString());
            } else if (entidadObjeto instanceof Paquete paquete) {
                auditoria.setIdEntidad(String.valueOf(paquete.getIdPaquete()));
                auditoria.setDescripcion("Paquete afectado: " + paquete.getNombre());
                auditoria.setDatosAdicionales(paquete.toString());
            } else {
                auditoria.setDescripcion("Entidad afectada: " + entidad);
            }

            auditoriaService.registrarAuditoria(auditoria);

        } catch (Exception e) {
            log.error("Error registrando auditoría automática", e);
        }
    }
}

 */
