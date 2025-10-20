package com.laboratorio.repository;

import com.laboratorio.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface UsuarioRepository extends JpaRepository< Usuario, Long> {

    // Si usas JPA
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> buscarUsuariosPorNombreCoincidente(@Param("nombre") String nombre);

    Usuario findByUsername(String usuario);

    public Usuario findByIdUsuario(int id);

    Usuario findByUsernameAndPassword(String username, String Password);

    public boolean existsByUsername(String username);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByActivoFalse();

    //List<Usuario> findByRolNombre(String rol);
    List<Usuario> findByNombre(String nombre);

}
