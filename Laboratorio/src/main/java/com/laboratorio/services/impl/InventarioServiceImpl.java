package com.laboratorio.services.impl;

import com.laboratorio.model.ExamenInsumo;
import com.laboratorio.model.Inventario;
import com.laboratorio.repository.ExamenInsumoRepository;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.SolicitudDetalleRepository;
import com.laboratorio.service.InventarioService;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
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

        LocalDate hoy = LocalDate.now();

        if (entity.getStockBloqueado() > entity.getStockActual()) {
            throw new IllegalArgumentException(
                    "El Stock Bloqueado no puede ser mayor que el Stock Actual."
            );
        }

        if (entity.getStockMinimo() > entity.getStockActual()) {
            throw new IllegalArgumentException(
                    "El Stock Mínimo no puede ser mayor que el Stock Actual."
            );
        }

        if (entity.getFechaApertura() != null && entity.getFechaApertura().isAfter(hoy)) {
            throw new IllegalArgumentException(
                    "La Fecha de Apertura no puede ser posterior a la fecha actual."
            );
        }

        if (entity.getFechaVencimiento() != null && entity.getFechaVencimiento().isBefore(hoy)) {
            throw new IllegalArgumentException(
                    "La Fecha de Vencimiento no puede estar vencida."
            );
        }

        /*
        if (entity.getIdInventario() == null) {
            if (inventarioRepository.existsByInsumo_IdInsumo(entity.getInsumo().getIdInsumo())) {
                throw new IllegalArgumentException("Este insumo ya tiene un registro en inventario.");
            }
        } else {
            Inventario existente = inventarioRepository.findByInsumo_IdInsumo(entity.getInsumo().getIdInsumo());

            if (existente != null
                    && !existente.getIdInventario().equals(entity.getIdInventario())) {
                throw new IllegalArgumentException("Otro inventario ya está asociado a este insumo.");
            }
        }
         */
        inventarioRepository.save(entity);
    }

    @Override
    public void delete(Inventario entity) {
        inventarioRepository.delete(entity);
    }

    @Override
    public void ajustarInventarioPorCita(Long idCita, String nuevoEstado) {
        List<Long> examenes = solicitudDetalleRepository.findExamenesByCita(idCita);

        if (examenes == null || examenes.isEmpty()) {
            return;
        }

        ajustarInventarioPorExamenes(examenes, nuevoEstado);
    }

    @Override
    public void ajustarInventarioPorExamenes(List<Long> idsExamen, String estado) {
        if (idsExamen == null || idsExamen.isEmpty()) {
            return;
        }

        List<ExamenInsumo> insumosRequeridos = examenInsumoRepository.findByExamenInList(idsExamen);

        if (insumosRequeridos == null || insumosRequeridos.isEmpty()) {
            return;
        }

        LocalDate hoy = LocalDate.now();
        String estadoNormalizado = (estado == null) ? "" : estado.toUpperCase();

        for (ExamenInsumo req : insumosRequeridos) {
            Long idInsumo = req.getIdInsumo();
            int cantidadNecesaria = req.getCantidadNecesaria();

            switch (estadoNormalizado) {
                case "AGENDADA":
                case "CONFIRMADA": {
                    Inventario inv = inventarioRepository.buscarInventarioParaBloqueo(
                            idInsumo, hoy, cantidadNecesaria
                    );

                    if (inv == null) {
                        throw new IllegalArgumentException(
                                "No existe un lote válido con stock suficiente para el insumo ID " + idInsumo
                        );
                    }

                    inv.setStockBloqueado(inv.getStockBloqueado() + cantidadNecesaria);
                    inventarioRepository.save(inv);
                    break;
                }

                case "TERMINADA": {
                    consumirBloqueadoEnLotes(idInsumo, cantidadNecesaria, hoy);
                    break;
                }

                case "CANCELADA": {
                    liberarBloqueadoEnLotes(idInsumo, cantidadNecesaria, hoy);
                    break;
                }

                default:
                    break;
            }
        }
    }

    @Override
    public void reajustarInventarioPorCambio(Long idCita, String estadoAnterior, String estadoNuevo,
            List<Long> examenesAnteriores, List<Long> examenesNuevos) {

        String anterior = (estadoAnterior == null) ? "" : estadoAnterior.toUpperCase();
        String nuevo = (estadoNuevo == null) ? "" : estadoNuevo.toUpperCase();

        boolean mismosExamenes = (examenesAnteriores == null && examenesNuevos == null)
                || (examenesAnteriores != null && examenesAnteriores.equals(examenesNuevos));

        if (anterior.equals(nuevo) && mismosExamenes) {
            return;
        }

        // Revertir lo anterior solo si era estado que bloqueaba
        switch (anterior) {
            case "AGENDADA":
            case "CONFIRMADA":
                ajustarInventarioPorExamenes(examenesAnteriores, "CANCELADA");
                break;

            case "TERMINADA":
                // No se revierte automáticamente porque ya hubo consumo real
                break;

            default:
                break;
        }

        // Aplicar lo nuevo
        switch (nuevo) {
            case "AGENDADA":
            case "CONFIRMADA":
            case "TERMINADA":
            case "CANCELADA":
                ajustarInventarioPorExamenes(examenesNuevos, nuevo);
                break;

            default:
                break;
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

    private void liberarBloqueadoEnLotes(Long idInsumo, int cantidadNecesaria, LocalDate hoy) {
        List<Inventario> inventarios = inventarioRepository.buscarLotesConBloqueado(idInsumo, hoy);

        int totalBloqueado = inventarios.stream()
                .mapToInt(Inventario::getStockBloqueado)
                .sum();

        if (totalBloqueado < cantidadNecesaria) {
            throw new IllegalArgumentException(
                    "No existe stock bloqueado suficiente para liberar el insumo ID " + idInsumo
            );
        }

        int restante = cantidadNecesaria;

        for (Inventario inv : inventarios) {
            if (inv.getStockBloqueado() <= 0) {
                continue;
            }

            int aLiberar = Math.min(inv.getStockBloqueado(), restante);
            inv.setStockBloqueado(inv.getStockBloqueado() - aLiberar);
            inventarioRepository.save(inv);

            restante -= aLiberar;

            if (restante == 0) {
                break;
            }
        }
    }

    private void consumirBloqueadoEnLotes(Long idInsumo, int cantidadNecesaria, LocalDate hoy) {
        List<Inventario> inventarios = inventarioRepository.buscarLotesConBloqueado(idInsumo, hoy);

        int totalBloqueado = inventarios.stream()
                .mapToInt(Inventario::getStockBloqueado)
                .sum();

        if (totalBloqueado < cantidadNecesaria) {
            throw new IllegalArgumentException(
                    "No existe stock bloqueado suficiente para consumir el insumo ID " + idInsumo
            );
        }

        int restante = cantidadNecesaria;

        for (Inventario inv : inventarios) {
            if (inv.getStockBloqueado() <= 0) {
                continue;
            }

            int aConsumir = Math.min(inv.getStockBloqueado(), restante);

            inv.setStockBloqueado(inv.getStockBloqueado() - aConsumir);
            inv.setStockActual(inv.getStockActual() - aConsumir);

            inventarioRepository.save(inv);

            restante -= aConsumir;

            if (restante == 0) {
                break;
            }
        }
    }
}
