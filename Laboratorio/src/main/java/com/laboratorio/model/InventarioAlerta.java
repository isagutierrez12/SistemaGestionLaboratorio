package com.laboratorio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Data;

public class InventarioAlerta {

    private Long idInventario;
    private String codigoBarras;
    private String nombreInsumo;
    private String tipoInsumo;
    private int stockActual;
    private int stockMinimo;
    private LocalDate fechaVencimiento;
    private Long diasParaVencer;
    private boolean bajoStock;
    private boolean proximoVencer;
    private String etiquetaAlerta;
    
    public Long getIdInventario() { return idInventario; }
    public void setIdInventario(Long idInventario) { this.idInventario = idInventario; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombreInsumo() { return nombreInsumo; }
    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }

    public String getTipoInsumo() { return tipoInsumo; }
    public void setTipoInsumo(String tipoInsumo) { this.tipoInsumo = tipoInsumo; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Long getDiasParaVencer() { return diasParaVencer; }
    public void setDiasParaVencer(Long diasParaVencer) { this.diasParaVencer = diasParaVencer; }

    public boolean isBajoStock() { return bajoStock; }
    public void setBajoStock(boolean bajoStock) { this.bajoStock = bajoStock; }

    public boolean isProximoVencer() { return proximoVencer; }
    public void setProximoVencer(boolean proximoVencer) { this.proximoVencer = proximoVencer; }

    public String getEtiquetaAlerta() { return etiquetaAlerta; }
    public void setEtiquetaAlerta(String etiquetaAlerta) { this.etiquetaAlerta = etiquetaAlerta; }

}
