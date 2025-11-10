package com.laboratorio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@Table(name = "auditoria_criticos")
public class AuditoriaCriticos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;  

    @Column(name = "usuario", nullable = false, length = 100)
    private String usuario; 

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "fecha_hora", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fechaHora = new Date();

    @Column(name = "tiempo_activo")
    private Integer tiempoActivo;
}
