package com.laboratorio.service;

import com.laboratorio.model.Inventario;
import com.laboratorio.model.InventarioAlerta;
import com.laboratorio.repository.InventarioRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InventarioAlertaService {

    private final InventarioRepository inventarioRepository;

    public InventarioAlertaService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<InventarioAlerta> obtenerAlertas(String tipo) {

        LocalDate hoy = LocalDate.now();
        LocalDate limiteVencimiento = hoy.plusDays(30);

        List<Inventario> inventarios = inventarioRepository.findAll();

        return inventarios.stream()
                .map(inv -> mapearAlerta(inv, hoy, limiteVencimiento))
                .filter(Objects::nonNull)
                .filter(dto -> filtrarPorTipo(dto, tipo))
                .sorted(Comparator
                        .comparing((InventarioAlerta dto) -> dto.isBajoStock() ? 0 : 1)
                        .thenComparing(dto -> dto.getDiasParaVencer() == null ? Long.MAX_VALUE : dto.getDiasParaVencer())
                )
                .collect(Collectors.toList());
    }

    private InventarioAlerta mapearAlerta(Inventario inv, LocalDate hoy, LocalDate limite) {

        int umbralBajoStock = (int) Math.ceil(inv.getStockMinimo() + (inv.getStockMinimo() * 0.10));
        boolean bajoStock = inv.getStockActual() <= umbralBajoStock;

        boolean proximoVencer = inv.getFechaVencimiento() != null
                && !inv.getFechaVencimiento().isBefore(hoy)
                && !inv.getFechaVencimiento().isAfter(limite);

        if (!bajoStock && !proximoVencer) {
            return null;
        }

        InventarioAlerta dto = new InventarioAlerta();
        dto.setIdInventario(inv.getIdInventario());
        dto.setCodigoBarras(inv.getCodigoBarras());
        dto.setNombreInsumo(inv.getInsumo().getNombre());
        dto.setTipoInsumo(inv.getInsumo().getTipo());
        dto.setStockActual(inv.getStockActual());
        dto.setStockMinimo(inv.getStockMinimo());
        dto.setFechaVencimiento(inv.getFechaVencimiento());

        if (inv.getFechaVencimiento() != null) {
            dto.setDiasParaVencer(ChronoUnit.DAYS.between(hoy, inv.getFechaVencimiento()));
        }

        dto.setBajoStock(bajoStock);
        dto.setProximoVencer(proximoVencer);

        if (bajoStock && proximoVencer) {
            dto.setEtiquetaAlerta("Bajo stock y próximo a vencer");
        } else if (bajoStock) {
            dto.setEtiquetaAlerta("Bajo stock");
        } else {
            dto.setEtiquetaAlerta("Próximo a vencer");
        }

        return dto;
    }

    private boolean filtrarPorTipo(InventarioAlerta dto, String tipo) {
        if (tipo == null || tipo.isBlank() || "TODAS".equalsIgnoreCase(tipo)) {
            return true;
        }
        return switch (tipo.toUpperCase()) {
            case "BAJO_STOCK" ->
                dto.isBajoStock();
            case "VENCIMIENTO" ->
                dto.isProximoVencer();
            default ->
                true;
        };
    }

}
