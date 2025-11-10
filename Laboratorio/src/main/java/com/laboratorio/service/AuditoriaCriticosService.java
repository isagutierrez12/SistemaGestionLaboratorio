/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.model.Rol;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.AuditoriaCriticosRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaCriticosService {

    private final AuditoriaCriticosRepository auditoriaCriticosRepository;
    private final UsuarioService usuarioService;
    private final RolService rolService;

    @Autowired
    public AuditoriaCriticosService(AuditoriaCriticosRepository auditoriaCriticosRepository,
            UsuarioService usuarioService,
            RolService rolService) {
        this.auditoriaCriticosRepository = auditoriaCriticosRepository;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    public List<AuditoriaCriticos> listarCriticas() {
        return auditoriaCriticosRepository.findAllOrderByFechaHoraDesc();
    }

    public List<AuditoriaCriticos> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;
        return auditoriaCriticosRepository.buscarPorRangoFechas(inicio, fin);
    }

    public List<AuditoriaCriticos> buscarAuditoria(String query) {
        return auditoriaCriticosRepository.buscarPorQuery(query);
    }

    public void registrarAuditoriaCritica(AuditoriaCriticos auditoria) {
        Usuario usuarioQueHaceCambio = usuarioService.getUsuarioPorUsername(auditoria.getUsuario());

        if (usuarioQueHaceCambio != null) {
            auditoria.setUsuarioId(usuarioQueHaceCambio.getIdUsuario());
        } else {
            throw new IllegalArgumentException(
                    "No se encontró el usuario que realiza la acción con username: " + auditoria.getUsuario()
            );
        }
        auditoriaCriticosRepository.save(auditoria);
    }

    public void registrarCambioRoles(Usuario usuarioDestino, String rolAsignado, String usuarioQueHaceCambio) {
        AuditoriaCriticos auditoria = new AuditoriaCriticos();

        auditoria.setUsuario(usuarioQueHaceCambio);
        auditoria.setUsuarioId(usuarioService.getUsuarioPorUsername(usuarioQueHaceCambio).getIdUsuario());
        auditoria.setTipoEvento("CAMBIO_ROLES");

        auditoria.setDescripcion("Se cambió/Asignó el rol [" + rolAsignado + "] al usuario: "
                + usuarioDestino.getUsername() + " [ID: " + usuarioDestino.getIdUsuario() + "]");

        auditoriaCriticosRepository.save(auditoria);
    }

    public AuditoriaCriticos buscarUltimoLoginActivo(String username) {
        return auditoriaCriticosRepository.findAllOrderByFechaHoraDesc().stream()
                .filter(a -> a.getUsuario().equals(username)
                && "LOGIN".equals(a.getTipoEvento())
                && a.getTiempoActivo() == null)
                .findFirst()
                .orElse(null);
    }
}
