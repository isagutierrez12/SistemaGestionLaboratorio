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

        // Buscar el último login de este usuario que no tenga tiempo activo
      //  List<AuditoriaCriticos> logins = auditoriaCriticosService.buscarPorQuery(username);
       // AuditoriaCriticos ultimoLogin = logins.stream()
        //        .filter(a -> a.getTipoEvento().equals("LOGIN") && a.getTiempoActivo() == null)
         //       .findFirst()
          //      .orElse(null);

      //   if (ultimoLogin != null) {
            // Calcular tiempo activo en minutos
       //      long tiempoActivoMillis = new Date().getTime() - ultimoLogin.getFechaHora().getTime();
        //     int minutos = (int) (tiempoActivoMillis / 1000 / 60);

            // Crear registro de logout
            AuditoriaCriticos auditoria = new AuditoriaCriticos();
            auditoria.setUsuario(username);
        //     auditoria.setUsuarioId(ultimoLogin.getUsuarioId());
            auditoria.setTipoEvento("LOGOUT");
            auditoria.setDescripcion("El usuario cerró sesión en el sistema.");
            auditoria.setFechaHora(new Date());
       //      auditoria.setTiempoActivo(minutos);

            // Guardar registro de logout
            auditoriaCriticosService.registrarAuditoriaCritica(auditoria);

            // También podrías actualizar el login si quieres
       //      ultimoLogin.setTiempoActivo(minutos);
        //     auditoriaCriticosService.registrarAuditoriaCritica(ultimoLogin);
        }
    }
// }

