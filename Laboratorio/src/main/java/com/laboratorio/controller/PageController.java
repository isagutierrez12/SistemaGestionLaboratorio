
package com.laboratorio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

 @Controller
public class PageController {
    @GetMapping("/{page}")
    public String mostrar(@PathVariable String page){
        return page;
    }
}
