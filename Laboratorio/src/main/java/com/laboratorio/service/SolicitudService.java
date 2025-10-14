/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Paciente;
import com.laboratorio.model.Solicitud;
import com.laboratorio.model.SolicitudDetalle;
import java.util.List;

public interface SolicitudService  extends CrudService<Solicitud>{
      List<Solicitud> buscarSolicitudes(String query);

}
