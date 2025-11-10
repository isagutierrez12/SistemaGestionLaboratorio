package com.laboratorio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.laboratorio.service.InventarioService;

@Component
public class InventarioTestRunner implements CommandLineRunner {

    @Autowired
    private InventarioService inventarioService;

    @Override
    public void run(String... args) throws Exception {
        Long idCita = 2L; // usa el id real de tu BD
        String estadoCita = "TERMINADA"; // o "TERMINADA" o "CANCELADA"

        System.out.println("🔹 Ejecutando prueba de ajuste de inventario...");
        inventarioService.ajustarInventarioPorCita(idCita, estadoCita);
        System.out.println("Ajuste completado. ");
    }
}
