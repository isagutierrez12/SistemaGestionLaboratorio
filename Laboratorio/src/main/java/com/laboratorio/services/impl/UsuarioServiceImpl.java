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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl  implements UsuarioService{

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

 

    @Override
    @Transactional
    public void save(Usuario usuario, boolean crearRolUser) {
        usuario=usuarioRepository.save(usuario);
        if (crearRolUser) {  //Si se está creando el usuario, se crea el rol por defecto "USER"
            Rol rol = new Rol();
            rol.setNombre("USER");
            rol.setIdUsuario(usuario.getIdUsuario());
            rolRepository.save(rol);
        }
    }

    @Override
    @Transactional
    public void delete(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }
    
    
    @Override
    @Transactional
    public String desactivarUsuario(Long idUsuario) {
        try {
            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                return "Usuario no encontrado";
            }
            if (!usuario.isActivo()) {
                return "El usuario ya está inactivo";
            }
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return "Usuario desactivado correctamente";
        } catch (Exception e) {
        // Captura cualquier error al actualizar el usuario
        return "Error al cambiar el estado del usuario. Intente nuevamente";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }
    @Override
    @Transactional
    public String reactivarUsuario(Long idUsuario) {
        try {
            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                return "Usuario no encontrado";
            }
            if (usuario.isActivo()) {
                return "El usuario ya está activo";
            }
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            return "Usuario activado correctamente";
        } catch (Exception e) {
            return "Error al cambiar el estado del usuario. Intente nuevamente";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosInactivos() {
        return usuarioRepository.findByActivoFalse();
    }
   @Override
   @Transactional(readOnly = true)
   public List<Usuario> buscarUsuariosPorNombre(String nombre){
       return usuarioRepository.findByNombre(nombre);
   }
}