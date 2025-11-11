package com.laboratorio.security;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.service.AuditoriaCriticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuditoriaLoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final AuditoriaCriticosService auditoriaCriticosService;

    @Autowired
    public AuditoriaLoginListener(AuditoriaCriticosService auditoriaCriticosService) {
        this.auditoriaCriticosService = auditoriaCriticosService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();

        AuditoriaCriticos auditoria = new AuditoriaCriticos();
        auditoria.setUsuario(username);
        auditoria.setTipoEvento("LOGIN");
        auditoria.setDescripcion("El usuario inició sesión en el sistema.");
        auditoria.setFechaHora(new Date());
        auditoria.setTiempoActivo(null);

        auditoriaCriticosService.registrarAuditoriaCritica(auditoria);
    }
}

