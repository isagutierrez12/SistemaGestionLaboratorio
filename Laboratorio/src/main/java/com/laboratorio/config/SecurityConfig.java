package com.laboratorio.config;

import com.laboratorio.model.Ruta;
import com.laboratorio.service.RutaPermitService;
import com.laboratorio.service.RutaService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            RutaPermitService rutaPermitService,
            RutaService rutaService) throws Exception {

        final String[] rutaPermit = rutaPermitService.getRutaPermitsString();
        final List<Ruta> rutas = rutaService.getAll();

        http.authorizeHttpRequests(auth -> {
            if (rutaPermit != null && rutaPermit.length > 0) {
                auth.requestMatchers(rutaPermit).permitAll();
            }
            for (Ruta ruta : rutas) {
                auth.requestMatchers(ruta.getRuta()).hasRole(ruta.getRoleName());
            }
            auth.anyRequest().authenticated();
        })
                .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
                );

        return http.build();
    }

}
