package com.laboratorio;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void guardarYBuscarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Melanie");
        usuario.setPrimerApellido("Gutierrez");
        usuario.setSegundoApellido("Prueba");
        usuario.setUsuario("mgutierrez");
        usuario.setPassword("1234");
        usuario.setActivo(true);
        usuario.setFechaCreacion(new Date());

        Usuario guardado = usuarioRepository.save(usuario);

        assertThat(guardado.getIdUsuario()).isNotNull();

        Usuario encontrado = usuarioRepository.findById(guardado.getIdUsuario()).orElse(null);
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNombre()).isEqualTo("Melanie");
    }
}
