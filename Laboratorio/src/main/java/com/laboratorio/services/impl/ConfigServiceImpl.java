/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Config;
import com.laboratorio.repository.ConfigRepository;
import com.laboratorio.service.ConfigService;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService{

    private final ConfigRepository configuracionRepository;

    public ConfigServiceImpl(ConfigRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }
    @Override
    public int getDiasAnticipacion() {
        return configuracionRepository.findByClave("DIAS_ANTICIPACION")
                .map(config -> Integer.parseInt(config.getValor()))
                .orElse(14);
    }
    @Override
    public void actualizarDiasAnticipacion(int dias) {
        if (dias != 7 && dias != 14 && dias != 21) {
            throw new IllegalArgumentException("Valor no permitido. Solo se aceptan 7, 14 o 21 días.");
        }

        Config config = configuracionRepository.findByClave("DIAS_ANTICIPACION")
                .orElseGet(() -> {
                    Config nueva = new Config();
                    nueva.setCategoria("ALERTAS");
                    nueva.setClave("DIAS_ANTICIPACION");
                    nueva.setDescripcion("Días de anticipación para alertas de vencimiento");
                    return nueva;
                });

        config.setValor(String.valueOf(dias));
        configuracionRepository.save(config);
    }

    
}
