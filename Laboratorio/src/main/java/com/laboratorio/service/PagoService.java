/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Pago;

public interface PagoService{
    void save(Pago pago);
    boolean existsByCita(Long idCita);
    
    Pago saveOrUpdateByCita(Long idCita, Double monto, String tipoPago);
}
