package com.laboratorio.controller;

import com.laboratorio.model.Notificacion;
import com.laboratorio.model.Usuario;
import com.laboratorio.service.NotificacionService;
import com.laboratorio.service.NotificacionUsuarioService;
import com.laboratorio.service.UsuarioService;
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

    @Autowired
    private NotificacionUsuarioService notificacionUsuarioService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/vencimientos")
    @ResponseBody
    public List<Notificacion> listarNotificaciones(Authentication authentication) {

        if (authentication == null) {
            return List.of();
        }

        Usuario usuario = usuarioService.getUsuarioPorUsername(authentication.getName());

        return notificacionUsuarioService
                .obtenerNotificacionesNoLeidas(usuario.getIdUsuario());
    }

    private boolean esAdministrador(Authentication authentication) {

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_ADMIN"));
    }

    @GetMapping("/todas")
    @ResponseBody
    public List<Notificacion> obtenerTodas(Authentication auth) {
        Usuario usuario = usuarioService.getUsuarioPorUsername(auth.getName());
        return notificacionUsuarioService.obtenerTodas(usuario.getIdUsuario());
    }

    // ✅ NUEVO: Endpoint para obtener contador de no leídas
    @GetMapping("/no-leidas/count")
    @ResponseBody
    public long obtenerContadorNoLeidas(Authentication auth) {
        Usuario usuario = usuarioService.getUsuarioPorUsername(auth.getName());
        return notificacionUsuarioService.contarNoLeidas(usuario.getIdUsuario());
    }

    // ✅ NUEVO: Endpoint combinado que devuelve ambos datos
    @GetMapping("/datos-completos")
    @ResponseBody
    public Map<String, Object> obtenerDatosCompletos(Authentication auth) {
        Usuario usuario = usuarioService.getUsuarioPorUsername(auth.getName());
        Long idUsuario = usuario.getIdUsuario();
        
        Map<String, Object> respuesta = new HashMap<>();
        
        // Obtener todas las notificaciones
        List<Notificacion> notificaciones = notificacionUsuarioService.obtenerTodas(idUsuario);
        
        // Contar las no leídas
        long totalNoLeidas = notificacionUsuarioService.contarNoLeidas(idUsuario);
        
        respuesta.put("notificaciones", notificaciones);
        respuesta.put("totalNoLeidas", totalNoLeidas);
        
        return respuesta;
    }

    @PostMapping("/marcar-leidas")
    @ResponseBody
    public void marcarComoLeidas(Authentication auth) {
        Usuario usuario = usuarioService.getUsuarioPorUsername(auth.getName());
        notificacionUsuarioService.marcarTodasComoLeidas(usuario.getIdUsuario());
    }
}