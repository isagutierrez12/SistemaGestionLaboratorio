package com.laboratorio.controller;

import com.laboratorio.model.Rol;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.RolService;
import com.laboratorio.service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolService rolService;

    @GetMapping("/usuarios")
    public String listadoUsuarios(Model model) {
        var lista = usuarioService.getUsuarios();
        model.addAttribute("usuarios", lista);
        return "/usuario/usuarios";
    }

    @GetMapping("/agregar")
    public String agregarUsuarios(Model model) {
        return "/usuario/agregar";
    }

    @GetMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, @RequestParam("rol") String rolSeleccionado,   Model model) {

        try {
            usuarioService.save(usuario, rolSeleccionado);
            return "redirect:/usuario/usuario"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/usuario/agregar"; 
        }
        
        
    }
    @GetMapping("/modificar/{idUsuario}")
    public String modicarUsuario(Usuario usuario, Model model){
        usuario = usuarioService.getUsuario(usuario);
        Rol rol = new Rol();
        rol = rolService.findRolByIdUsuario(usuario.getIdUsuario());
        model.addAttribute("rol", rol);
        model.addAttribute("usuario", usuario);
        
        return "usuario/modificar";
    }
}
