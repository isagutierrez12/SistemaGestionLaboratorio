package com.laboratorio;

import com.laboratorio.model.Ruta;
import com.laboratorio.service.RutaPermitService;
import com.laboratorio.service.RutaService;
import java.util.Arrays;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Autowired
    private RutaPermitService rutaPermitService;

    @Autowired
    private RutaService rutaService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] rutaPermit = rutaPermitService.getRutaPermitsString();
        List<Ruta> rutas = rutaService.getAll();

        System.out.println("Permits: " + Arrays.toString(rutaPermit));
        http.authorizeHttpRequests((request) -> {
            request.requestMatchers(rutaPermit).permitAll();
            for (Ruta ruta : rutas) {
                request.requestMatchers(ruta.getRuta())
                        .hasAuthority(ruta.getRoleName());
            }

        })
                .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/paciente/pacientes", true)
                .permitAll()
                )
                .logout((logout) -> logout
                .logoutSuccessUrl("/")
                .permitAll()
                ).sessionManagement(session -> session
                .invalidSessionUrl("/login?expired") 
                .maximumSessions(1) 
                .expiredUrl("/login?expired") 
                );
        return http.build();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configurerGlobla(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
