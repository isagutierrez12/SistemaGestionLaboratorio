package com.laboratorio.controller;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;

import com.laboratorio.model.Usuario;
import com.laboratorio.service.RolService;

import com.laboratorio.service.UsuarioService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String mensaje = usuarioService.desactivarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/usuario/usuarios";
    }

    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String mensaje = usuarioService.reactivarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/usuario/usuarios"; // página con usuarios desactivados
    }

    @GetMapping("/buscar")
    public String buscarUsuariosPorNombre(@RequestParam(value = "nombre", required = false) String nombre, Model model) {
        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("advertencia", "Debe ingresar al menos un criterio de búsqueda");
            return "usuario/usuarios"; // recarga la misma vista
        }
        try {
            List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
            if (usuarios.isEmpty()) {
                model.addAttribute("advertencia", "No existe ningún usuario con ese nombre.");
            }
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("nombreBuscado", nombre); // para resaltar coincidencias
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo realizar la búsqueda. Intente nuevamente.");
        }
        return "usuario/usuarios";
    }

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

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, @RequestParam("roles") String rolSeleccionado, Model model) {

        try {
            usuarioService.save(usuario, rolSeleccionado);
            return "redirect:/usuario/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/usuario/agregar";
        }

    }

    @GetMapping("/modificar/{idUsuario}")
    public String modicarUsuario(Usuario usuario, Model model) {
        usuario = usuarioService.getUsuario(usuario);

        String rol = usuario.getRoles().getFirst().getNombre();
        String rolSeleccionado;
        switch (rol) {
            case "ADMIN":
                rolSeleccionado = "1";
                break;
            case "REP":
                rolSeleccionado = "2";
                break;
            case "DOCTOR":
                rolSeleccionado = "3";
                break;
            default:
                rolSeleccionado = "";

        }

        model.addAttribute("rol", rolSeleccionado);
        model.addAttribute("usuario", usuario);
        return "usuario/modificar";
    }
}
