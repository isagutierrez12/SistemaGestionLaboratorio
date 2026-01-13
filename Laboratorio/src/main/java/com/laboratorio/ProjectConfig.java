package com.laboratorio;

import com.laboratorio.config.CustomAuthenticationFailureHandler;
import com.laboratorio.config.CustomAuthenticationSuccessHandler;
import com.laboratorio.model.Ruta;
import com.laboratorio.service.RutaPermitService;
import com.laboratorio.service.RutaService;
import com.laboratorio.security.UsuarioActivoFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Autowired
    private RutaPermitService rutaPermitService;

    @Autowired
    private RutaService rutaService;

    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioActivoFilter usuarioActivoFilter
    ) throws Exception {

        String[] rutaPermit = rutaPermitService.getRutaPermitsString();
        List<Ruta> rutas = rutaService.getAll();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/session/**"))
                .addFilterBefore(
                        usuarioActivoFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(rutaPermit).permitAll();
                    for (Ruta ruta : rutas) {
                        request.requestMatchers(ruta.getRuta())
                                .hasAnyRole(ruta.getRoleName());
                    }
                })
                .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .defaultSuccessUrl("/paciente/pacientes", true)
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?reason=logout")
                .permitAll()
                )
                .sessionManagement(session -> session
                .invalidSessionUrl("/login?reason=expired")
                .maximumSessions(1)
                .expiredUrl("/login?reason=expired")
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
