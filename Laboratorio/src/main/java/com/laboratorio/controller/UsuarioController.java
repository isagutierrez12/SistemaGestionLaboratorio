
package com.laboratorio.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @GetMapping("usuarios")
    public String listadoAdmin(Model model){
        return "/usuario/usuarios";
    }
}
