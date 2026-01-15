/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Notificacion;
import com.laboratorio.model.NotificacionUsuario;
import com.laboratorio.repository.NotificacionUsuarioRepository;
import com.laboratorio.service.NotificacionUsuarioService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionUsuarioServiceImpl implements NotificacionUsuarioService {

    @Autowired
    private NotificacionUsuarioRepository repo;

    @Override
    public List<Notificacion> obtenerNotificacionesNoLeidas(Long idUsuario) {
        return repo.obtenerNotificacionesNoLeidas(idUsuario);
    }

    @Override
      @Transactional
    public void marcarTodasComoLeidas(Long idUsuario) {
        repo.marcarTodasComoLeidas(idUsuario);
    }

    @Override
    public List<Notificacion> obtenerTodas(Long idUsuario) {
          return repo.obtenerNotificaciones(idUsuario);
    }
    @Override
    public long contarNoLeidas(Long idUsuario){
    return repo.countByUsuarioIdUsuarioAndLeidaFalse(idUsuario);
}

}
