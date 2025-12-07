/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.laboratorio.model;

import java.util.ArrayList;
import java.util.List;

public class DetalleCitaResponse {

    private List<Item> examenes = new ArrayList<>();
    private List<Item> paquetes = new ArrayList<>();

    public List<Item> getExamenes() {
        return examenes;
    }

    public void setExamenes(List<Item> examenes) {
        this.examenes = examenes;
    }

    public List<Item> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Item> paquetes) {
        this.paquetes = paquetes;
    }

    public static class Item {
        private String codigo;
        private String nombre;
        private String extra;

        public Item() {}

        public Item(String codigo, String nombre, String extra) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.extra = extra;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }
    }
}