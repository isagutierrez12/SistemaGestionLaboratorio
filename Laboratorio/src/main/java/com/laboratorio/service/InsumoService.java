package com.laboratorio.service;

import com.laboratorio.model.Insumo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface InsumoService extends CrudService<Insumo>{
    
    List<Insumo> buscarPorQuery(String query);

}
