/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.laboratorio.repository;
import com.laboratorio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 *
 * @author melanie
 */
@EnableJpaRepositories
public interface UsuarioRepository extends JpaRepository < Usuario, Long> {

}
