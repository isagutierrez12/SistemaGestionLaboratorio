package com.laboratorio;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.RutaPermitService;
import com.laboratorio.service.RutaService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RutaPermitService rutaPermitService;

    @MockBean
    private RutaService rutaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        Mockito.when(rutaPermitService.getRutaPermitsString()).thenReturn(new String[]{"/login", "/registro/nuevo"});
        Mockito.when(rutaService.getAll()).thenReturn(Collections.emptyList());

        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setNombre("Test");
        usuario.setPrimerApellido("User");
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
     @Test
    public void testLoginExitoso() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "123456")
                  .with(csrf()))
                .andExpect(status().is3xxRedirection()) // redirige después de login
                .andExpect(redirectedUrl("/")); // tu defaultSuccessUrl
    }

    @Test
    public void testLoginFallido() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "testuser")
                .param("password", "wrongpass")
                 .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}


