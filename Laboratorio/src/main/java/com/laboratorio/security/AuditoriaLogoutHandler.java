package com.laboratorio.security;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.service.AuditoriaCriticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class AuditoriaLogoutHandler implements LogoutSuccessHandler {

    private final AuditoriaCriticosService auditoriaCriticosService;

    @Autowired
    public AuditoriaLogoutHandler(AuditoriaCriticosService auditoriaCriticosService) {
        this.auditoriaCriticosService = auditoriaCriticosService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        if (authentication != null) {
            String username = authentication.getName();

            // Buscar último LOGIN activo
            AuditoriaCriticos ultimoLogin = auditoriaCriticosService.buscarUltimoLoginActivo(username);

            if (ultimoLogin != null) {
                long tiempoActivoMillis = new Date().getTime() - ultimoLogin.getFechaHora().getTime();
                int minutos = (int) (tiempoActivoMillis / 1000 / 60);

                // Crear registro LOGOUT
                AuditoriaCriticos auditoriaLogout = new AuditoriaCriticos();
                auditoriaLogout.setUsuario(username);
                auditoriaLogout.setUsuarioId(ultimoLogin.getUsuarioId());
                auditoriaLogout.setTipoEvento("LOGOUT");
                auditoriaLogout.setDescripcion("El usuario cerró sesión en el sistema.");
                auditoriaLogout.setFechaHora(new Date());
                auditoriaLogout.setTiempoActivo(minutos);

                auditoriaCriticosService.registrarAuditoriaCritica(auditoriaLogout);

                // Actualizar LOGIN original con tiempoActivo
                ultimoLogin.setTiempoActivo(minutos);
                auditoriaCriticosService.registrarAuditoriaCritica(ultimoLogin);
            }
        }

        // Redirigir al login o página principal
        response.sendRedirect("/");
    }
}