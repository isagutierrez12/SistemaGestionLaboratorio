package com.laboratorio.controller;

import com.laboratorio.model.Usuario;
import com.laboratorio.service.RolService;
import com.laboratorio.service.UsuarioService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/usuarios")
    public String listadoUsuarios(Model model) {
        var lista = usuarioService.getUsuarios();
        model.addAttribute("usuarios", lista);
        return "usuario/usuarios";
    }

    @GetMapping("/agregar")
    public String agregarUsuarios(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/usuario/agregar";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("roles") String rolSeleccionado,
            Model model,
            RedirectAttributes redirectAttributes) {

        boolean esNuevo = (usuario.getIdUsuario() == null);

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", rolSeleccionado);
            model.addAttribute("errores", result.getAllErrors());
            return esNuevo ? "/usuario/agregar" : "/usuario/modificar";
        }

        try {
            usuarioService.save(usuario, rolSeleccionado);

            redirectAttributes.addFlashAttribute("success",
                    esNuevo ? "Usuario registrado correctamente"
                            : "Usuario actualizado correctamente");

            return "redirect:/usuario/usuarios";

        } catch (IllegalArgumentException ex) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", rolSeleccionado);
            model.addAttribute("error", ex.getMessage());

            return esNuevo ? "/usuario/agregar" : "/usuario/modificar";
        }
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modicarUsuario(Usuario usuario, Model model) {
        usuario = usuarioService.getUsuario(usuario);

        String rol = usuario.getRoles().get(0).getNombre();
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
    public List<Usuario> buscarUsuariosJSON(@RequestParam(value = "query", required = false) String query) {

        if (query == null || query.trim().isEmpty()) {
            return usuarioService.getUsuariosActivos();
        }

        return usuarioService.buscarUsuariosPorQuery(query.trim());
    }

}
