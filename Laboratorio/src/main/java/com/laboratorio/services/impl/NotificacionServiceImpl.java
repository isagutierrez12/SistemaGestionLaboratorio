/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Inventario;
import com.laboratorio.model.Notificacion;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.NotificacionRepository;
import com.laboratorio.service.NotificacionService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ConfigServiceImpl configuracionService;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public void notificacionVencimiento() {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximo = hoy.plusDays(14);

        List<Inventario> proximos = inventarioRepository.findByFechaVencimientoBetween(hoy, limiteProximo);
        List<Inventario> vencidos = inventarioRepository.findByFechaVencimientoBefore(hoy);

        for (Inventario inv : proximos) {
            boolean existe = notificacionRepository.existsByInventarioAndLeidaFalse(inv);
            if (!existe) {
                Notificacion n = new Notificacion();
                n.setTitulo("Insumo próximo a vencer");
                n.setMensaje("El insumo '" + inv.getInsumo().getNombre() + "' vence el " + inv.getFechaVencimiento());
                n.setFechaCreacion(LocalDateTime.now());
                n.setLeida(false);
                n.setInventario(inv);
                notificacionRepository.save(n);
                System.out.println("Notificación creada: " + n.getMensaje());
            }
        }

        for (Inventario inv : vencidos) {
            boolean existe = notificacionRepository.existsByInventarioAndLeidaFalse(inv);
            if (!existe) {
                Notificacion n = new Notificacion();
                n.setTitulo("Insumo vencido");
                n.setMensaje("El insumo '" + inv.getInsumo().getNombre() + "' venció el " + inv.getFechaVencimiento());
                n.setFechaCreacion(LocalDateTime.now());
                n.setLeida(false);
                n.setInventario(inv);
                notificacionRepository.save(n);
                System.out.println("Notificación creada: " + n.getMensaje());
            }
        }
    }

    @PostConstruct
    @Override
    public List<Notificacion> obtenerNotificacionesRecientes() {
        LocalDateTime dosSemanas = LocalDateTime.now().minusWeeks(2);
        return notificacionRepository.findByFechaCreacionAfterOrderByFechaCreacionDesc(dosSemanas);
    }
    @PostConstruct
    @Override
    public void verificarInventarioBajoStock() {
        List<Inventario> inventarios = inventarioRepository.findAll();

        for (Inventario inv : inventarios) {
            if (!inv.isActivo()) continue;

            int stockReal = inv.getStockActual() - inv.getStockBloqueado();
            int umbral = inv.getStockMinimo() + 5;

            if (stockReal <= umbral) {

                boolean existe = notificacionRepository.existsByInventario_IdInventarioAndTituloContaining(
                        inv.getIdInventario(), "Stock bajo"
                );

                if (!existe) {
                    Notificacion n = new Notificacion();
                    n.setTitulo("Stock bajo en inventario");
                    n.setMensaje("El insumo '" + inv.getInsumo().getNombre() +
                                 "' tiene un stock real de " + stockReal + 
                                 " unidades, cerca del mínimo (" + inv.getStockMinimo() + ").");
                    n.setInventario(inv);
                    n.setLeida(false);
                    n.setFechaCreacion(LocalDateTime.now());
                    notificacionRepository.save(n);
                }
            }
        }
    }

}
