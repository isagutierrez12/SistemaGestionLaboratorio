
package com.laboratorio.controller;



import com.laboratorio.model.Usuario;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.UsuarioService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    UsuarioService usuarioService;
    
     @GetMapping("/usuarios")
    public String listadoUsuarios(Model model){
        try{
            var lista = usuarioService.getUsuarios();
            model.addAttribute("usuarios", lista);
        }
        catch (Exception e) {
        model.addAttribute("error", 
            "No se pudo cargar el módulo consultado. Intente nuevamente o contacte soporte.");
        }
            return "/usuario/usuarios";
    }
    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String mensaje = usuarioService.desactivarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/usuario/usuarios";
}
    @GetMapping("/reactivar/{id}")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String mensaje = usuarioService.reactivarUsuario(id);
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/usuario/inactivos"; // página con usuarios desactivados
    }
    @GetMapping("/buscar")
    public String buscarUsuariosPorNombre(@RequestParam(value = "nombre", required = false) String nombre,Model model) {
        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("advertencia", "Debe ingresar al menos un criterio de búsqueda");
            return "usuario/usuarios"; // recarga la misma vista
        }
        try {
            List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
            if (usuarios.isEmpty()) {
                model.addAttribute("advertencia", "No existe ningún usuario con ese nombre.");
            }
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("nombreBuscado", nombre); // para resaltar coincidencias
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo realizar la búsqueda. Intente nuevamente.");
        }
        return "usuario/usuarios";
    }
}


