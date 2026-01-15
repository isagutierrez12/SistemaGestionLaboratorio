package com.laboratorio.repository;

import com.laboratorio.model.Notificacion;
import com.laboratorio.model.NotificacionUsuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificacionUsuarioRepository
        extends JpaRepository<NotificacionUsuario, Long> {

    List<NotificacionUsuario>
            findByUsuarioIdUsuarioAndLeidaFalse(Long idUsuario);


    @Query("""
   SELECT nu.notificacion 
   FROM NotificacionUsuario nu 
   WHERE nu.usuario.idUsuario = :idUsuario 
   AND nu.leida = false
""")
    List<Notificacion> obtenerNotificacionesNoLeidas(@Param("idUsuario") Long idUsuario);

    List<NotificacionUsuario> findByUsuarioIdUsuarioOrderByNotificacionFechaCreacionDesc(Long idUsuario);

    long countByUsuarioIdUsuarioAndLeidaFalse(Long idUsuario);

    @Query("""
    SELECT nu.notificacion 
    FROM NotificacionUsuario nu 
    WHERE nu.usuario.idUsuario = :idUsuario
    ORDER BY nu.notificacion.fechaCreacion DESC
""")
    List<Notificacion> obtenerNotificaciones(Long idUsuario);

@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("""
    UPDATE NotificacionUsuario n 
    SET n.leida = true, n.fechaLectura = CURRENT_TIMESTAMP 
    WHERE n.usuario.idUsuario = :idUsuario 
    AND n.leida = false
""")
void marcarTodasComoLeidas(@Param("idUsuario") Long idUsuario);
    @Query("SELECT COUNT(n) FROM NotificacionUsuario n WHERE n.usuario.idUsuario = :idUsuario AND n.leida = false")
    Long contarNoLeidas(@Param("idUsuario") Long idUsuario);

    
}
