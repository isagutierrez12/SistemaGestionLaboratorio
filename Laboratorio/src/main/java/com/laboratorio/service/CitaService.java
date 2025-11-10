/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Cita;
import com.laboratorio.model.RutaPermit;

/**
 *
 * @author melanie
 */
public interface CitaService  extends CrudService<Cita>{
    Cita getById(Long idCita);
}
