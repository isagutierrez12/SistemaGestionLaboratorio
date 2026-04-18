package com.laboratorio.controller;

import com.laboratorio.model.PerfilForm;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String verPerfil(Model model, Authentication authentication) {
        model.addAttribute("page", "list");

        if (authentication != null) {
            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUsername(username);

            if (usuario != null && !model.containsAttribute("perfilForm")) {
                PerfilForm perfilForm = new PerfilForm();
                perfilForm.setNombre(usuario.getNombre());
                perfilForm.setPrimerApellido(usuario.getPrimerApellido());
                perfilForm.setSegundoApellido(usuario.getSegundoApellido());

                model.addAttribute("perfilForm", perfilForm);
            }
        }

        return "perfil/miPerfil";
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@Valid @ModelAttribute("perfilForm") PerfilForm perfilForm,
            BindingResult result,
            Authentication authentication,
            Model model,
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

        if (result.hasErrors()) {
            model.addAttribute("page", "list");
            model.addAttribute("abrirTab", "profile-edit");
            model.addAttribute("usuarioLogueado", usuario);
            return "perfil/miPerfil";
        }

        usuario.setNombre(perfilForm.getNombre().trim());
        usuario.setPrimerApellido(perfilForm.getPrimerApellido().trim());
        usuario.setSegundoApellido(perfilForm.getSegundoApellido().trim());

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
        return "redirect:/perfil";
    }

    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(
            @org.springframework.web.bind.annotation.RequestParam("actual") String actual,
            @org.springframework.web.bind.annotation.RequestParam("nueva") String nueva,
            @org.springframework.web.bind.annotation.RequestParam("confirmacion") String confirmacion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No hay un usuario autenticado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("mensaje", "No se encontró el usuario en el sistema.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        // Limpiar espacios
        actual = actual.trim();
        nueva = nueva.trim();
        confirmacion = confirmacion.trim();

        // 1. Validar contraseña actual
        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("mensaje", "La contraseña actual no es correcta.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        // 2. Validar que no sea igual a la actual
        if (actual.equals(nueva) || passwordEncoder.matches(nueva, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("mensaje", "La nueva contraseña no puede ser igual a la contraseña actual.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        // 3. Validar confirmación
        if (!nueva.equals(confirmacion)) {
            redirectAttributes.addFlashAttribute("mensaje", "La nueva contraseña y la confirmación no coinciden.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        // 4. Validar longitud
        if (nueva.length() < 6) {
            redirectAttributes.addFlashAttribute("mensaje", "La contraseña debe tener al menos 6 caracteres.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
            return "redirect:/perfil";
        }

        // 5. Guardar nueva contraseña
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("success", "La contraseña se actualizó correctamente.");
        redirectAttributes.addFlashAttribute("abrirTab", "profile-change-password");
        return "redirect:/perfil";
    }
}
