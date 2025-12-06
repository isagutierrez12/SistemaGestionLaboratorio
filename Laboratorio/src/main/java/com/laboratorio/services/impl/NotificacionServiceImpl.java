package com.laboratorio.services.impl;

import com.laboratorio.model.Inventario;
import com.laboratorio.model.Notificacion;
import com.laboratorio.model.NotificacionUsuario;
import com.laboratorio.model.Usuario;
import com.laboratorio.repository.InventarioRepository;
import com.laboratorio.repository.NotificacionRepository;
import com.laboratorio.repository.NotificacionUsuarioRepository;
import com.laboratorio.repository.UsuarioRepository;
import com.laboratorio.service.NotificacionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ConfigServiceImpl configuracionService;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionUsuarioRepository notificacionUsuarioRepository;

    @Transactional
    @Override
    public void notificacionVencimiento() {
        LocalDate hoy = LocalDate.now();
        LocalDate limiteProximo = hoy.plusDays(14);

        List<Inventario> proximos = inventarioRepository.findByFechaVencimientoBetween(hoy, limiteProximo);
        List<Inventario> vencidos = inventarioRepository.findByFechaVencimientoBefore(hoy);

        for (Inventario inv : proximos) {
            boolean existe = notificacionRepository.existsByInventarioAndLeidaFalse(inv);
            if (!existe) {
                Notificacion n = new Notificacion();
                n.setTitulo("Insumo próximo a vencer");
                n.setMensaje("El insumo '" + inv.getInsumo().getNombre() + "' vence el " + inv.getFechaVencimiento());
                n.setFechaCreacion(LocalDateTime.now());
                n.setLeida(false);
                n.setInventario(inv);
                notificacionRepository.save(n);
                List<Usuario> admins = usuarioRepository.findByRol("ADMIN");

                for (Usuario usuario : admins) {
                    NotificacionUsuario nu = new NotificacionUsuario();
                    nu.setUsuario(usuario);
                    nu.setNotificacion(n);
                    nu.setLeida(false);
                    nu.setFechaLectura(null);
                    notificacionUsuarioRepository.save(nu);
                }
                System.out.println("Notificación creada: " + n.getMensaje());
            }
        }

        for (Inventario inv : vencidos) {
            boolean existe = notificacionRepository.existsByInventarioAndLeidaFalse(inv);
            if (!existe) {
                Notificacion n = new Notificacion();
                n.setTitulo("Insumo vencido");
                n.setMensaje("El insumo '" + inv.getInsumo().getNombre() + "' venció el " + inv.getFechaVencimiento());
                n.setFechaCreacion(LocalDateTime.now());
                n.setLeida(false);
                n.setInventario(inv);
                notificacionRepository.save(n);
                List<Usuario> admins = usuarioRepository.findByRol("ADMIN");

                for (Usuario usuario : admins) {
                    NotificacionUsuario nu = new NotificacionUsuario();
                    nu.setUsuario(usuario);
                    nu.setNotificacion(n);
                    nu.setLeida(false);
                    nu.setFechaLectura(null);
                    notificacionUsuarioRepository.save(nu);
                }

                System.out.println("Notificación creada: " + n.getMensaje());
            }
        }
    }

    @Override
    public List<Notificacion> obtenerNotificacionesRecientes() {
        LocalDateTime dosSemanas = LocalDateTime.now().minusWeeks(2);
        return notificacionRepository.findByFechaCreacionAfterOrderByFechaCreacionDesc(dosSemanas);
    }

    @Transactional
    @Override
    public void verificarInventarioBajoStock() {
        List<Inventario> inventarios = inventarioRepository.findAll();

        for (Inventario inv : inventarios) {
            if (!inv.getActivo()) {
                continue;
            }
            int stockReal = inv.getStockActual() - inv.getStockBloqueado();
            int umbral = inv.getStockMinimo() + 5;

            if (stockReal <= umbral) {

                boolean existe = notificacionRepository.existsByInventario_IdInventarioAndTituloContaining(
                        inv.getIdInventario(), "Stock bajo"
                );

                if (!existe) {
                    Notificacion n = new Notificacion();
                    n.setTitulo("Stock bajo en inventario");
                    n.setMensaje("El insumo '" + inv.getInsumo().getNombre()
                            + "' tiene un stock real de " + stockReal
                            + " unidades, cerca del mínimo (" + inv.getStockMinimo() + ").");
                    n.setInventario(inv);
                    n.setLeida(false);
                    n.setFechaCreacion(LocalDateTime.now());
                    notificacionRepository.save(n);
                    List<Usuario> admins = usuarioRepository.findByRol("ADMIN");

                    for (Usuario usuario : admins) {
                        NotificacionUsuario nu = new NotificacionUsuario();
                        nu.setUsuario(usuario);
                        nu.setNotificacion(n);
                        nu.setLeida(false);
                        nu.setFechaLectura(null);
                        notificacionUsuarioRepository.save(nu);
                    }
                }
            }
        }
    }

    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private final Map<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    @Transactional
    public void registrarIntentoFallido(String username, String ip) {
        int intentos = intentosFallidos.getOrDefault(username, 0) + 1;
        intentosFallidos.put(username, intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            String titulo = "Alerta de Seguridad - Intentos Fallidos";
            String mensaje = String.format(
                    "El usuario '%s' ha excedido el límite de intentos fallidos de inicio de sesión. "
                    + "Se han detectado %d intentos fallidos desde la IP %s. "
                    + "Se recomienda verificar la actividad sospechosa.",
                    username, intentos, ip
            );

            Notificacion alerta = new Notificacion("SEGURIDAD_INTENTOS", titulo, mensaje, ip);
            notificacionRepository.save(alerta);
            List<Usuario> admins = usuarioRepository.findByRol("ADMIN");

            for (Usuario usuario : admins) {
                NotificacionUsuario nu = new NotificacionUsuario();
                nu.setUsuario(usuario);
                nu.setNotificacion(alerta);
                nu.setLeida(false);
                nu.setFechaLectura(null);
                notificacionUsuarioRepository.save(nu);
            }

            System.out.println("ALERTA GENERADA: " + mensaje);
        }
    }

    public void resetearIntentosFallidos(String username) {
        intentosFallidos.remove(username);
    }

    @Transactional
    public void registrarEliminacionMasiva(String entidad, int cantidad, String usuario, String ip) {
        if (cantidad > 10) {
            String titulo = "Alerta - Eliminación Masiva Detectada";
            String mensaje = String.format(
                    "Se ha detectado una eliminación masiva de %d registros en la entidad '%s'. "
                    + "Usuario responsable: %s. IP de origen: %s",
                    cantidad, entidad, usuario, ip
            );

            Notificacion alerta = new Notificacion("SEGURIDAD_ELIMINACION", titulo, mensaje, ip);
            notificacionRepository.save(alerta);
            List<Usuario> admins = usuarioRepository.findByRol("ADMIN");

            for (Usuario admin : admins) {
                NotificacionUsuario nu = new NotificacionUsuario();
                nu.setUsuario(admin);
                nu.setNotificacion(alerta);
                nu.setLeida(false);
                nu.setFechaLectura(null);
                notificacionUsuarioRepository.save(nu);
            }
        }
    }

    public List<Notificacion> obtenerAlertasSeguridad() {
        return notificacionRepository.findByTipoStartingWithOrderByFechaCreacionDesc("SEGURIDAD");
    }

    public List<Notificacion> obtenerTodasLasNotificaciones() {
        return notificacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Transactional
    public void marcarTodasComoLeidas(Long idUsuario) {
        notificacionUsuarioRepository.marcarTodasComoLeidas(idUsuario);
    }

}
