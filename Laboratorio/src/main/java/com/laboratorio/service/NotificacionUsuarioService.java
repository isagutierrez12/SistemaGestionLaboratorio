/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Notificacion;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface NotificacionUsuarioService {
    public void marcarTodasComoLeidas(Long idUsuario); 
     List<Notificacion> obtenerNotificacionesNoLeidas(Long idUsuario);
     @Transactional
     public List<Notificacion> obtenerTodas(Long idUsuario);
     long contarNoLeidas(Long idUsuario);
     
}
