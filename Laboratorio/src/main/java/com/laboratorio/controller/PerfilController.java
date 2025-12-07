package com.laboratorio.controller;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        model.addAttribute("page", "list");
        return "perfil/miPerfil";
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@RequestParam String nombre,
            @RequestParam String primerApellido,
            @RequestParam String segundoApellido,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No hay un usuario autenticado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/perfil";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el usuario en el sistema.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/perfil";
        }

        usuario.setNombre(nombre.trim());
        usuario.setPrimerApellido(primerApellido.trim());
        usuario.setSegundoApellido(segundoApellido.trim());

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam("actual") String actual,
            @RequestParam("nueva") String nueva,
            @RequestParam("confirmacion") String confirmacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No hay un usuario autenticado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/perfil";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el usuario en el sistema.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/perfil";
        }

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("mensaje", "La contraseña actual no es correcta.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/perfil";
        }

        if (!nueva.equals(confirmacion)) {
            redirectAttributes.addFlashAttribute("mensaje", "La nueva contraseña y la confirmación no coinciden.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            return "redirect:/perfil";
        }

        if (nueva.length() < 6) {
            redirectAttributes.addFlashAttribute("mensaje", "La contraseña debe tener al menos 6 caracteres.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            return "redirect:/perfil";
        }

        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "La contraseña se actualizó correctamente.");
        return "redirect:/perfil";
    }
}
