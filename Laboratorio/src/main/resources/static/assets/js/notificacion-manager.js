
class NotificacionManager {
    constructor() {
        this.notificacionesLeidas = new Set();
        this.cargarEstadoUsuario();
    }

    async cargarEstadoUsuario() {
        const leidasLocal = localStorage.getItem('notificacionesLeidas');
        if (leidasLocal) {
            this.notificacionesLeidas = new Set(JSON.parse(leidasLocal));
        }

        await this.sincronizarConServidor();
    }

    async sincronizarConServidor() {
        try {
            const response = await fetch('/notificaciones/contador-usuario');
            const data = await response.json();
            
            this.actualizarCampanita(data.count);
        } catch (error) {
            console.error('Error sincronizando con servidor:', error);
        }
    }

    async marcarComoLeida(idNotificacion) {
        this.notificacionesLeidas.add(idNotificacion);

        localStorage.setItem('notificacionesLeidas', JSON.stringify([...this.notificacionesLeidas]));
        

        await fetch(`/notificaciones/${idNotificacion}/marcar-vista-usuario`, { 
            method: 'POST' 
        });
        
        this.actualizarCampanita();
    }
}

