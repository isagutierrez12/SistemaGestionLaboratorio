package com.laboratorio.controller;

import com.laboratorio.model.Usuario;
import com.laboratorio.service.RolService;
import com.laboratorio.service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    RolService rolService;

    //listado
    @GetMapping("/usuarios")
    public String listadoUsuarios(Model model) {
        var lista = usuarioService.getUsuarios();
        model.addAttribute("usuarios", lista);
        return "/usuario/usuarios";
    }

    //agregar
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
    //editar

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
        return "redirect:/usuario/inactivos"; // página con usuarios desactivados
    }

    @GetMapping("/buscarJSON")
    @ResponseBody
    public List<Usuario> buscarUsuariosJSON(@RequestParam(value = "nombre", required = false) String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return usuarioService.getUsuarios(); // muestra todos si está vacío
        }
        // Aquí hacemos la búsqueda que contenga la cadena (ignore case)
        return usuarioService.buscarUsuariosPorNombreCoincidente(nombre.trim());
    }

}
