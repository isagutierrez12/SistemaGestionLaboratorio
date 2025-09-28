/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.services.impl;

import com.laboratorio.model.Rol;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.RolRepository;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.UsuarioService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuario(Usuario usuario) {
        return usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioPorUsernameYPassword(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void save(Usuario usuario, String rolSeleccionado) {

        if (usuario.getIdUsuario() == null) {
            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya está ocupado");
            }
            usuario.setFechaCreacion(new Date());
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!usuario.getPassword().isEmpty()) {
                existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
            existente.setNombre(usuario.getNombre());
            existente.setPrimerApellido(usuario.getPrimerApellido());
            existente.setSegundoApellido(usuario.getSegundoApellido());
            existente.setUsername(usuario.getUsername());

            usuario = existente;

            usuario = usuarioRepository.save(usuario);

            Rol rol = new Rol();
            switch (rolSeleccionado) {
                case "1" -> {
                    rol.setNombre("ADMIN");
                    rol.setIdUsuario(usuario.getIdUsuario());
                }
                case "2" -> {
                    rol.setNombre("REP");
                    rol.setIdUsuario(usuario.getIdUsuario());
                }
                case "3" -> {
                    rol.setNombre("DOCTOR");
                    rol.setIdUsuario(usuario.getIdUsuario());
                }
                default ->
                    throw new IllegalArgumentException("Rol no válido: " + rolSeleccionado);
            }

            rolRepository.save(rol);
        }
    }

    @Override
    @Transactional
    public void delete(Usuario usuario) {

        usuarioRepository.delete(usuario);
    }

}
