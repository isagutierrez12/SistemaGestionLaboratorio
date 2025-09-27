
package com.laboratorio.controller;



import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    UsuarioService usuarioService;
    
     @GetMapping("/usuarios")
    public String listadoUsuarios(Model model){
        var lista = usuarioService.getUsuarios();
        model.addAttribute("usuarios", lista);
        return "/usuario/usuarios";
    }
}
