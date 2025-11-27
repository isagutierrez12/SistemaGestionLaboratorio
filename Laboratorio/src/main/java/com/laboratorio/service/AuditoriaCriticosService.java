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
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    public List<AuditoriaCriticos> buscarPorUsuario(String usuario) {
        return auditoriaCriticosRepository.buscarPorUsuario(usuario);
    }

    public List<AuditoriaCriticos> buscarPorTipoEvento(String tipoEvento) {
        return auditoriaCriticosRepository.buscarPorTipoEvento(tipoEvento);
    }

    public List<AuditoriaCriticos> filtrar(String usuario, String tipoEvento,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        List<AuditoriaCriticos> auditorias;

        // Manejo de fechas según disponibilidad
        if (fechaInicio != null && fechaFin != null) {
            auditorias = auditoriaCriticosRepository.buscarPorRangoFechas(fechaInicio, fechaFin);
        } else if (fechaInicio != null) {
            auditorias = auditoriaCriticosRepository.buscarDesdeFecha(fechaInicio);
        } else if (fechaFin != null) {
            auditorias = auditoriaCriticosRepository.buscarHastaFecha(fechaFin);
        } else {
            auditorias = auditoriaCriticosRepository.findAllOrderByFechaHoraDesc();
        }

        // Filtrado por usuario
        if (usuario != null && !usuario.isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getUsuario().toLowerCase().contains(usuario.toLowerCase()))
                    .toList();
        }

        // Filtrado por tipo de evento
        if (tipoEvento != null && !tipoEvento.isEmpty()) {
            auditorias = auditorias.stream()
                    .filter(a -> a.getTipoEvento().toLowerCase().contains(tipoEvento.toLowerCase()))
                    .toList();
        }

        return auditorias;
    }

    public void exportarExcel(List<AuditoriaCriticos> auditorias, OutputStream os) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Auditoría Críticos");

        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Usuario", "Tipo Evento", "Fecha", "Descripción"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (AuditoriaCriticos a : auditorias) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(a.getId());
            row.createCell(1).setCellValue(a.getUsuario());
            row.createCell(2).setCellValue(a.getTipoEvento());
            row.createCell(3).setCellValue(a.getFechaHora().toString());
            row.createCell(4).setCellValue(a.getDescripcion());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

}
