/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.config;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.NotificacionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        String username = authentication.getName();
        String ip = request.getRemoteAddr();

        notificacionService.resetearIntentosFallidos(username);

        Usuario usuario = usuarioRepository.findByUsername(username);
        request.getSession().setAttribute("usuarioLogueado", usuario);

        System.out.println("Login exitoso para: " + username + " desde IP: " + ip);

        response.sendRedirect("/paciente/pacientes");
    }
}
