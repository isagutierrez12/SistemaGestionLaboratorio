package com.laboratorio.service;

import com.laboratorio.model.Usuario;
import java.util.List;

public interface UsuarioService {

    public List<Usuario> getUsuarios();

    public Usuario getUsuario(Usuario usuario);

    public Usuario getUsuarioPorUsername(String username);

    public Usuario getUsuarioPorUsernameYPassword(String username, String password);

    public void save(Usuario usuario, String rolSeleccionado);

    public void delete(Usuario usuario);

    public String desactivarUsuario(Long idUsuario);

    public List<Usuario> getUsuariosActivos();

    public String reactivarUsuario(Long idUsuario);

    public List<Usuario> getUsuariosInactivos();

    List<Usuario> buscarUsuariosPorQuery(String query);

    public Usuario buscarPorUsername(String username);

}
