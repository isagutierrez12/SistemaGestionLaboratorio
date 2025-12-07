package com.laboratorio.controller;

import com.laboratorio.model.Paquete;
import com.laboratorio.repository.DetallePaqueteRepository;
import com.laboratorio.service.PaqueteService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    private final PaqueteService paqueteService;
    private final DetallePaqueteRepository detallePaqueteRepository;

    @Autowired
    public PageController(PaqueteService paqueteService, DetallePaqueteRepository detallePaqueteRepository) {
        this.paqueteService = paqueteService;
        this.detallePaqueteRepository = detallePaqueteRepository;
    }

    @GetMapping("/{page}")
    public String mostrar(@PathVariable String page) {
        return page;
    }

    @GetMapping("/examenes")
    public String mostrarExamenes(Model model) {

        List<Paquete> paquetes = paqueteService.getActivosConExamenes();

        Map<Long, List<String>> examenesPorPaquete = new HashMap<>();

        for (Paquete p : paquetes) {
            List<String> nombresExamenes = detallePaqueteRepository
                    .findByPaqueteIdPaquete(p.getIdPaquete())
                    .stream()
                    .map(dp -> dp.getExamen().getNombre())
                    .toList();

            examenesPorPaquete.put(p.getIdPaquete(), nombresExamenes);
        }

        model.addAttribute("paquetes", paquetes);
        model.addAttribute("examenesPorPaquete", examenesPorPaquete);

        return "examenes";
    }
}
