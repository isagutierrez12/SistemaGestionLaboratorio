package com.laboratorio;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.model.Usuario;
import com.laboratorio.service.AuditoriaCriticosService;
import com.laboratorio.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditoriaCriticosAspect {

    @Autowired
    private AuditoriaCriticosService auditoriaCriticosService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired(required = false)
    private HttpServletRequest request;

    @AfterReturning("execution(* com.laboratorio.services.impl.UsuarioServiceImpl.save(..)) && args(usuario, ..)")
    public void auditarCambioRol(JoinPoint joinPoint, Usuario usuario) {
        try {
            // Obtenemos el usuario que realiza la acción
            String usuarioQueHaceCambio = (request != null && request.getUserPrincipal() != null)
                    ? request.getUserPrincipal().getName()
                    : "Desconocido";

            // Solo registrar auditoría si NO es cambio de rol
            // En save() ya estamos registrando los cambios de rol mediante registrarCambioRoles()
            // Por lo tanto, aquí podemos omitir CAMBIO_ROLES
            // Si quieres mantener otras auditorías, solo hazlas para eventos distintos
            // Ejemplo: LOGIN, LOGOUT, UPDATE_USUARIO
            // --- Comentamos lo que genera el duplicado ---
            // AuditoriaCriticos auditoria = new AuditoriaCriticos();
            // auditoria.setUsuarioId(usuario.getIdUsuario());
            // auditoria.setUsuario(usuarioQueHaceCambio);
            // auditoria.setTipoEvento("CAMBIO_ROLES");
            // auditoria.setDescripcion("Roles actuales del usuario: " + usuario.getRoles());
            // auditoria.setFechaHora(new Date());
            // auditoriaCriticosService.registrarAuditoriaCritica(auditoria);
        } catch (Exception e) {
            log.error("Error registrando auditoría de cambio de rol", e);
        }
    }

}
