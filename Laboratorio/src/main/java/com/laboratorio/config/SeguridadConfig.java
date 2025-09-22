
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SeguridadConfig {

    /**
     *
     * @return
     */
 

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/about", "/contact", "/css/**", "/js/**").permitAll() // público
                .requestMatchers("/admin/**").hasRole("ADMIN") // solo admin
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // user o admin
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // tu vista personalizada
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
}

