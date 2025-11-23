package com.laboratorio;

import com.laboratorio.model.Auditoria;
import com.laboratorio.model.Paciente;
import com.laboratorio.model.Examen;
import com.laboratorio.model.Insumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.model.Paquete;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.InsumoRepository;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.PacienteRepository;
import com.laboratorio.repository.PaqueteRepository;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditoriaAspect {

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
