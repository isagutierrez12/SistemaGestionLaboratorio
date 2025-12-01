package com.laboratorio.repository;

import com.laboratorio.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@EnableJpaRepositories
public interface UsuarioRepository extends JpaRepository< Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> buscarUsuariosPorNombreCoincidente(@Param("nombre") String nombre);

    @Query("""
    SELECT DISTINCT u FROM Usuario u
    LEFT JOIN u.roles r
    WHERE 
        LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(u.cedula) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<Usuario> buscarUsuariosPorQuery(@Param("query") String query);

    Usuario findByUsername(String usuario);

    public Usuario findByIdUsuario(int id);

    Usuario findByUsernameAndPassword(String username, String Password);

    public boolean existsByUsername(String username);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByActivoFalse();

    List<Usuario> findByNombre(String nombre);
    
    boolean existsByCedula(String cedula);


}
