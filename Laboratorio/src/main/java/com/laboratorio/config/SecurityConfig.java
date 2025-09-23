/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.config;
import com.laboratorio.model.Ruta;
import com.laboratorio.service.RutaPermitService;
import com.laboratorio.service.RutaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;



/**
 *
 * @author melanie
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer{
    @Autowired
    private RutaPermitService rutaPermitService;

    @Autowired
    private RutaService rutaService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       
        String[] rutaPermit = rutaPermitService.getRutaPermitsString();
        List<Ruta> rutas = rutaService.getAll();
        
        http.authorizeHttpRequests((request)->{request.requestMatchers(rutaPermit).permitAll();
        for (Ruta ruta : rutas){
            request.requestMatchers(ruta.getRuta()).hasRole(ruta.getRoleName());
        }
        })
        
                .formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                )
                .logout((logout) -> logout
                .logoutSuccessUrl("/")
                .permitAll()
                );
        return http.build();
    }

    @Autowired
    private UserDetailsService userDetailsService;

 
}
