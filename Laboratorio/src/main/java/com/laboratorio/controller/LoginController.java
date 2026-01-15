package com.laboratorio.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String reason,
            Model model
    ) {

        if ("inactivo".equals(reason)) {
            model.addAttribute("mensaje",
                    "Tu sesión se cerró automáticamente porque tu usuario fue desactivado por un administrador.");
            model.addAttribute("tipo", "warning");

        } else if ("expired".equals(reason)) {
            model.addAttribute("mensaje",
                    "Tu sesión expiró por inactividad. Por favor inicia sesión nuevamente.");
            model.addAttribute("tipo", "info");

        } else if ("logout".equals(reason)) {
            model.addAttribute("mensaje",
                    "Has cerrado sesión correctamente.");
            model.addAttribute("tipo", "success");
        }

        return "login";
    }
}
