package com.laboratorio.controller;

import com.laboratorio.config.SessionTimeoutHandler;
import com.laboratorio.model.SessionStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
@Slf4j
public class SessionController {
    
    private final SessionTimeoutHandler sessionTimeoutHandler;
    
    @GetMapping("/status")
    public ResponseEntity<?> getSessionStatus(HttpServletRequest request) {
        try {
            // Verificar si la sesión es válida
            boolean isSessionValid = request.getSession(false) != null;
            
            if (!isSessionValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("active", false);
                response.put("message", "Session expired");
                return ResponseEntity.ok(response);
            }
            
            long timeUntilExpiry = sessionTimeoutHandler.getTimeUntilExpiry();
            boolean showWarning = sessionTimeoutHandler.shouldShowWarning();
            long remainingTimeInSeconds = sessionTimeoutHandler.getRemainingTimeInSeconds();
            
            SessionStatusDTO sessionStatus = new SessionStatusDTO(
                true,
                timeUntilExpiry,
                showWarning,
                sessionTimeoutHandler.getWarningTimeInMinutes(),
                remainingTimeInSeconds
            );
            
            log.debug("Session status checked - Active: {}, Time until expiry: {} ms", 
                     true, timeUntilExpiry);
            
            return ResponseEntity.ok(sessionStatus);
            
        } catch (Exception e) {
            log.error("Error checking session status", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("active", false);
            errorResponse.put("error", "Error checking session status");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/extend")
    public ResponseEntity<?> extendSession(HttpServletRequest request) {
        try {
            // Simplemente acceder a la sesión la renueva
            var session = request.getSession(false);
            
            if (session != null) {
                // Tactear la sesión (esto renueva el último acceso)
                session.setAttribute("lastAccess", System.currentTimeMillis());
                log.debug("Session extended for session ID: {}", session.getId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Sesión extendida satisfactoriamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No hay sesión activa");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error extending session", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error extendiendo la sesión");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/invalidate")
    public ResponseEntity<?> invalidateSession(HttpServletRequest request) {
        try {
            var session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                log.debug("Sesión invalidada manualmente");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sesión invalidada satisfactoriamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error validado la sesión", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error validado la sesión");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}