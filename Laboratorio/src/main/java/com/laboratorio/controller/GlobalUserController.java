package com.laboratorio.controller;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@ControllerAdvice
public class GlobalUserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @ModelAttribute("usuarioLogueado")
    public Usuario usuarioLogueado(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String username = authentication.getName();
        return usuarioRepository.findByUsername(username);
    }
}
