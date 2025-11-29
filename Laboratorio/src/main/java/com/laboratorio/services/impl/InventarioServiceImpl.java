package com.laboratorio.services.impl;

import com.laboratorio.model.ExamenInsumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.repository.ExamenInsumoRepository;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.service.InventarioService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ExamenInsumoRepository examenInsumoRepository;

    @Autowired
    private SolicitudDetalleRepository solicitudDetalleRepository;

    @Override
    public List<Inventario> getAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario get(Inventario entity) {
        return inventarioRepository.findById(entity.getIdInventario()).orElse(null);
    }

    @Override
    public void save(Inventario entity) {
        inventarioRepository.save(entity);
    }

    @Override
    public void delete(Inventario entity) {
        inventarioRepository.delete(entity);
    }

    @Override
    public void ajustarInventarioPorCita(Long idCita, String nuevoEstado) {

        List<Long> examenes = solicitudDetalleRepository.findExamenesByCita(idCita);

        if (examenes.isEmpty()) {
            return;
        }

        List<ExamenInsumo> insumosRequeridos = examenInsumoRepository.findByExamenInList(examenes);

        for (ExamenInsumo req : insumosRequeridos) {
            Inventario inv = inventarioRepository.findByInsumo_IdInsumo(req.getIdInsumo());

            if (inv == null) {
                continue;
            }

            switch (nuevoEstado.toUpperCase()) {
                case "AGENDADA":

                    inv.setStockBloqueado(inv.getStockBloqueado() + req.getCantidadNecesaria());
                    break;

                case "TERMINADA":

                    inv.setStockActual(inv.getStockActual() - req.getCantidadNecesaria());
                    inv.setStockBloqueado(Math.max(0, inv.getStockBloqueado() - req.getCantidadNecesaria()));
                    break;

                case "CANCELADA":

                    inv.setStockBloqueado(Math.max(0, inv.getStockBloqueado() - req.getCantidadNecesaria()));
                    break;
            }

            inventarioRepository.save(inv);
        }
    }

    @Override
    public List<Inventario> buscarInventarioPorQuery(String query) {
        return inventarioRepository.buscarInventarioPorQuery(query);
    }

    @Override
    public void exportarExcel(List<Inventario> inventarios, OutputStream os) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Inventario");

        String[] columnas = {
            "Código Barras", "Insumo", "Tipo",
            "Stock Actual", "Stock Mínimo", "Fecha Vencimiento"
        };

        Row header = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        int idx = 1;

        for (Inventario inv : inventarios) {

            Row row = sheet.createRow(idx++);

            row.createCell(0).setCellValue(inv.getCodigoBarras());
            row.createCell(1).setCellValue(inv.getInsumo().getNombre());
            row.createCell(2).setCellValue(inv.getInsumo().getTipo());
            row.createCell(3).setCellValue(inv.getStockActual());
            row.createCell(4).setCellValue(inv.getStockMinimo());
            row.createCell(5).setCellValue(
                    inv.getFechaVencimiento() != null
                    ? inv.getFechaVencimiento().toString()
                    : ""
            );
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

}
