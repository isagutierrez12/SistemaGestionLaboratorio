/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Cita;
import com.laboratorio.model.Pago;
import com.laboratorio.model.PagoRow;
import com.laboratorio.repository.PagoRepository;
import com.laboratorio.service.CitaService;
import com.laboratorio.service.PagoService;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final CitaService citaService;

    public PagoServiceImpl(PagoRepository pagoRepository, CitaService citaService) {
        this.pagoRepository = pagoRepository;
        this.citaService = citaService;
    }

    @Override
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }

    @Override
    public boolean existsByCita(Long idCita) {
        return pagoRepository.existsByCita_IdCita(idCita);
    }

    @Override
    @Transactional
    public Pago saveOrUpdateByCita(Long idCita, Double monto, String tipoPago) {

        Pago pago = pagoRepository.findByCita_IdCita(idCita)
                .orElseGet(Pago::new);

        // si es nuevo, amarrarlo a la cita
        if (pago.getIdPago() == null) {
            Cita cita = citaService.getById(idCita);
            pago.setCita(cita);
        }

        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(monto);
        pago.setTipoPago(tipoPago);

        return pagoRepository.save(pago);
    }

    @Override
    public void exportarPagosExcel(List<PagoRow> pagos, OutputStream os) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Pagos registrados - Laboratorio");

        Row header = sheet.createRow(0);

        String[] columns = {"Fecha y hora", "Paciente", "Monto (CRC)", "Método de pago"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;

        for (PagoRow p : pagos) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(p.getFechaCita() != null ? p.getFechaCita().toString() : "");
            row.createCell(1).setCellValue(p.getPaciente() != null ? p.getPaciente() : "");
            row.createCell(2).setCellValue(p.getMonto() != null ? p.getMonto().doubleValue() : 0.0);
            row.createCell(3).setCellValue(p.getTipoPago() != null ? p.getTipoPago() : "");
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
    }
}
