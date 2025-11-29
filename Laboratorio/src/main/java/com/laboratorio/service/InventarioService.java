package com.laboratorio.service;

import com.laboratorio.model.Inventario;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public interface InventarioService extends CrudService<Inventario> {

    public void ajustarInventarioPorCita(Long idCita, String nuevoEstado);

    List<Inventario> buscarInventarioPorQuery(String query);

    void exportarExcel(List<Inventario> inventarios, OutputStream os) throws IOException;

}
