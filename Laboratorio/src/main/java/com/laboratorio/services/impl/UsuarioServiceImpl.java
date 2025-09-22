
package com.laboratorio.services.impl;

import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl  implements UsuarioService{

    private final UsuarioRepository usuarioRepository;
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario get(Usuario entity) {
        return usuarioRepository.findById(entity.getIdUsuario()).orElse(null);
    }

    @Override
    public void save(Usuario entity) {
        usuarioRepository.save(entity);
    }

    @Override
    public void delete(Usuario entity) {
        usuarioRepository.delete(entity);
    }
    
}
