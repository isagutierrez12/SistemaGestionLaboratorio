/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.laboratorio.service;

import com.laboratorio.model.Pago;
import com.laboratorio.model.PagoRow;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface PagoService{
    void save(Pago pago);
    boolean existsByCita(Long idCita);
    
    Pago saveOrUpdateByCita(Long idCita, Double monto, String tipoPago);
    void exportarPagosExcel(List<PagoRow> pagos, OutputStream os) throws IOException;
}
