
package com.laboratorio.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
   
     @GetMapping("agregar")
    public String agregar(Model model){
        
        return "/usuario/agregar";
    }
}
