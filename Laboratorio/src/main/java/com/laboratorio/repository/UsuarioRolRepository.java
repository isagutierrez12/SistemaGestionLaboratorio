
package com.laboratorio.repository;

import com.laboratorio.model.Usuario;
import com.laboratorio.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface UsuarioRolRepository extends JpaRepository < UsuarioRol, Long> {
    UsuarioRol findByUsuario(Usuario usuario);
}

