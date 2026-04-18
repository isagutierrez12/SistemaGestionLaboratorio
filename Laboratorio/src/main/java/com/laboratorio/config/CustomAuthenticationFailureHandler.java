/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.config;

import com.laboratorio.service.NotificacionService;
import com.laboratorio.service.UsuarioService;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public CustomAuthenticationFailureHandler(NotificacionService notificacionService,
            UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String username = request.getParameter("username");
        String ip = request.getRemoteAddr();

        String password = request.getParameter("password");

        if (username != null && !username.trim().isEmpty()) {
            notificacionService.registrarIntentoFallido(username, ip);

            System.out.println("Intento fallido para: " + username + " desde IP: " + ip);
        }
        String errorMessage = "Credenciales incorrectas";

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            errorMessage = "Debe completar todos los campos";
        }
        if (!usuarioService.existsByUsername(username)) {
            errorMessage = "El usuario no existe";
        } else if (exception.getMessage().contains("Bad credentials")) {
            errorMessage = "La contraseña es incorrecta";
        } else if (exception.getMessage().contains("User is disabled")) {
            errorMessage = "La cuenta está desactivada";
        } else if (exception.getMessage().contains("locked")) {
            errorMessage = "La cuenta está bloqueada";
        }

        response.sendRedirect("/login?error="
                + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}
