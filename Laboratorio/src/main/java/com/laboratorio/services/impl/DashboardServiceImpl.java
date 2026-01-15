/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Dashboard;
import com.laboratorio.model.ExamenTop;
import com.laboratorio.repository.CitaRepository;
import com.laboratorio.repository.ExamenRepository;
import com.laboratorio.repository.PagoRepository;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.repository.SolicitudRepository;
import com.laboratorio.service.DashboardService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CitaRepository citaRepository;
    private final SolicitudDetalleRepository detalleRepository;
    private final ExamenRepository examenRepository;
    private final PagoRepository pagoRepository;

    @Autowired
    public DashboardServiceImpl(CitaRepository citaRepository,
            SolicitudDetalleRepository detalleRepository, ExamenRepository examenRepository, PagoRepository pagoRepository) {
        this.citaRepository = citaRepository;
        this.detalleRepository = detalleRepository;
        this.examenRepository = examenRepository;
        this.pagoRepository = pagoRepository;
    }

    @Override
    public List<String> obtenerAreas() {
        return examenRepository.obtenerAreasUnicas();
    }

    @Override
    public Dashboard obtenerResumen(LocalDateTime inicio, LocalDateTime fin) {

        Double ingresosRaw = citaRepository.calcularIngresosPorCita(inicio, fin);
        Long pacientes = citaRepository.contarPacientesAtendidosPorCita(inicio, fin);

        Long totalIndiv = detalleRepository.contarExamenesIndividualesEnPeriodo(inicio, fin);
        Long totalPaq = detalleRepository.contarExamenesEnPaquetesEnPeriodo(inicio, fin);

        Double ingresosConfirmadosRaw = pagoRepository.sumMontoEnRango(inicio, fin);

        if (ingresosRaw == null) {
            ingresosRaw = 0.0;
        }
        if (pacientes == null) {
            pacientes = 0L;
        }
        if (totalIndiv == null) {
            totalIndiv = 0L;
        }
        if (totalPaq == null) {
            totalPaq = 0L;
        }

        if (ingresosConfirmadosRaw == null) {
            ingresosConfirmadosRaw = 0.0;
        }

        Long totalExamenes = totalIndiv + totalPaq;

        double promedio = 0.0;
        if (pacientes > 0) {
            promedio = totalExamenes.doubleValue() / pacientes.doubleValue();
        }

        Dashboard dto = new Dashboard();
        dto.setIngresosTotales(BigDecimal.valueOf(ingresosRaw));
        dto.setPacientesAtendidos(pacientes);
        dto.setPromedioExamenesPorPaciente(promedio);
        dto.setIngresosConfirmados(ingresosConfirmadosRaw);

        return dto;
    }

    @Override
    public List<ExamenTop> obtenerTopExamenes(LocalDateTime inicio,
            LocalDateTime fin,
            String area,
            int limite) {

        String areaParam = (area == null || area.isBlank()) ? null : area.toLowerCase();

        List<ExamenTop> listaIndiv = detalleRepository.topExamenesIndividuales(inicio, fin, areaParam);
        List<ExamenTop> listaPaq = detalleRepository.topExamenesDesdePaquetes(inicio, fin, areaParam);

        Map<String, Long> acumulado = new HashMap<>();

        if (listaIndiv != null) {
            for (ExamenTop e : listaIndiv) {
                acumulado.merge(e.getNombreExamen(), e.getCantidad(), Long::sum);
            }
        }

        if (listaPaq != null) {
            for (ExamenTop e : listaPaq) {
                acumulado.merge(e.getNombreExamen(), e.getCantidad(), Long::sum);
            }
        }

        return acumulado.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limite)
                .map(e -> new ExamenTop(e.getKey(), e.getValue()))
                .toList();
    }
}
