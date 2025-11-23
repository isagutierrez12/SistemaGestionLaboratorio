/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Auditoria;
import com.laboratorio.model.AuditoriaCriticos;
import com.laboratorio.repository.AuditoriaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    @Autowired
    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAll();

    }

    public List<Auditoria> listarAuditorias() {
        return auditoriaRepository.findAllOrderByFechaHoraDesc();
    }

    public List<Auditoria> buscarAuditoria(String query) {
        return auditoriaRepository.buscarPorQuery(query);
    }

    public void registrarAuditoria(Auditoria auditoria) {
        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> buscarPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;
        return auditoriaRepository.buscarPorRangoFechas(inicio, fin);
    }

}
