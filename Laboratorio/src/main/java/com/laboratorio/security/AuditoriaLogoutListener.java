package com.laboratorio.security;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.service.AuditoriaCriticosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import org.springframework.security.authentication.event.LogoutSuccessEvent;

@Component
public class AuditoriaLogoutListener implements ApplicationListener<LogoutSuccessEvent> {

    private final AuditoriaCriticosService auditoriaCriticosService;

    @Autowired
    public AuditoriaLogoutListener(AuditoriaCriticosService auditoriaCriticosService) {
        this.auditoriaCriticosService = auditoriaCriticosService;
    }

    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        // Crear registro de logout
        AuditoriaCriticos auditoria = new AuditoriaCriticos();
        auditoria.setUsuario(username);
        //     auditoria.setUsuarioId(ultimoLogin.getUsuarioId());
        auditoria.setTipoEvento("LOGOUT");
        auditoria.setDescripcion("El usuario cerró sesión en el sistema.");
        auditoria.setFechaHora(new Date());

        // Guardar registro de logout
        auditoriaCriticosService.registrarAuditoriaCritica(auditoria);

    }
}
