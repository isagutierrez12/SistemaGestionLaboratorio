/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Notificacion;
import com.laboratorio.service.NotificacionService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired
    private NotificacionService notificacionService;
    
    
    @GetMapping("/vencimientos")
    @ResponseBody
    public List<Notificacion> listarNotificaciones(Model model, Principal principal, Authentication authentication){
        if (authentication == null) return List.of();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN"));
        System.out.println(isAdmin);
        if(!isAdmin){
            return List.of();
        }
        notificacionService.notificacionVencimiento();
        System.out.println(notificacionService.obtenerNotificacionesRecientes().toString());
        return notificacionService.obtenerNotificacionesRecientes(); 
    }
    

}
