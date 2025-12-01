/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Notificacion;
import com.laboratorio.model.Usuario;
import java.util.List;

public interface NotificacionService {

    public void notificacionVencimiento();

    public List<Notificacion> obtenerNotificacionesRecientes();

    public void verificarInventarioBajoStock();
    public void registrarIntentoFallido(String username, String ip);

    public void resetearIntentosFallidos(String username);

    public void registrarEliminacionMasiva(String entidad, int cantidad, String usuario, String ip);

    public List<Notificacion> obtenerAlertasSeguridad();
    public List<Notificacion> obtenerTodasLasNotificaciones();


  
}
