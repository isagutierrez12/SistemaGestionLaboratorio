/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Notificacion;
import com.laboratorio.model.Usuario;
import java.util.List;

/**
 *
 * @author melanie
 */
public interface NotificacionService {
    public void notificacionVencimiento();
    public List<Notificacion> obtenerNotificacionesRecientes();
    public void verificarInventarioBajoStock();
 
}
