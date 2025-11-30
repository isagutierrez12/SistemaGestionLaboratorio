/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.config;

import com.laboratorio.service.NotificacionService;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;


@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    @Autowired
    private NotificacionService notificacionService;

   
    public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        
        String username = request.getParameter("username");
        String ip = request.getRemoteAddr();
        
        if (username != null && !username.trim().isEmpty()) {
            notificacionService.registrarIntentoFallido(username, ip);
            
            System.out.println("Intento fallido para: " + username + " desde IP: " + ip);
        }
        response.sendRedirect("/login?error=true");
    }
}