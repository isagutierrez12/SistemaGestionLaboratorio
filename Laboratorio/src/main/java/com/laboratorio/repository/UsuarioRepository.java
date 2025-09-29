package com.laboratorio.repository;

import com.laboratorio.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface UsuarioRepository extends JpaRepository< Usuario, Long> {

    Usuario findByUsername(String usuario);

    public Usuario findByIdUsuario(int id);

    Usuario findByUsernameAndPassword(String username, String Password);

    List<Usuario> findByActivoTrue();
    
    List<Usuario> findByActivoFalse();
    
    //List<Usuario> findByRolNombre(String rol);
    
    List<Usuario> findByNombre(String nombre);
}
