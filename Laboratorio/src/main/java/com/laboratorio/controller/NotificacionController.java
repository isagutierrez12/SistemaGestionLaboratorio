/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.controller;

import com.laboratorio.model.Notificacion;
import com.laboratorio.service.NotificacionService;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/seguridad")
    @ResponseBody
    public List<Notificacion> listarAlertasSeguridad(Authentication authentication) {
        if (!esAdministrador(authentication)) {
            return List.of();
        }
        return notificacionService.obtenerAlertasSeguridad();
    }
    

    @PostMapping("/intento-fallido")
    @ResponseBody
    public ResponseEntity<Map<String, String>> registrarIntentoFallido(
            @RequestParam String username,
            @RequestParam String ip) {
        
        notificacionService.registrarIntentoFallido(username, ip);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Intento fallido registrado");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/resetear-intentos")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetearIntentos(@RequestParam String username) {
        notificacionService.resetearIntentosFallidos(username);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Intentos reseteados para usuario: " + username);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/todas")
    @ResponseBody
    public List<Notificacion> listarTodasNotificaciones(Authentication authentication) {
        if (!esAdministrador(authentication)) {
            return List.of();
        }
        return notificacionService.obtenerTodasLasNotificaciones();
    }
    

    private boolean esAdministrador(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN"));
    }

}
