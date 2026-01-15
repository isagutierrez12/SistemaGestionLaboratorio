package com.laboratorio.security;

import com.laboratorio.service.UsuarioService;
import com.laboratorio.model.Usuario;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UsuarioActivoFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            String username = auth.getName();
            Usuario usuario = usuarioService.buscarPorUsername(username);

            if (usuario != null && Boolean.FALSE.equals(usuario.getActivo())) {

                // Usar logout de Spring Security para redirigir correctamente
                new SecurityContextLogoutHandler().logout(request, response, auth);

                // Redirigir con nuestro parámetro personalizado
                response.sendRedirect("/login?reason=inactivo");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
