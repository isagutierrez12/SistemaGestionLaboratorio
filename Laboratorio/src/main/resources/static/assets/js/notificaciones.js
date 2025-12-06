document.addEventListener("DOMContentLoaded", () => {
    function getCsrfToken() {
        const metaToken = document.querySelector('meta[name="_csrf"]');
        if (metaToken) {
            return metaToken.getAttribute('content');
        }

        const cookies = document.cookie.split(';');
        for (let cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'XSRF-TOKEN' || name === '_csrf') {
                return decodeURIComponent(value);
            }
        }

        const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
        if (csrfHeader) {
            const headerName = csrfHeader.getAttribute('content');
            console.warn('CSRF header encontrado pero no implementado:', headerName);
        }

        console.error('No se encontró token CSRF');
        return null;
    }

    function getCsrfHeader() {
        const metaHeader = document.querySelector('meta[name="_csrf_header"]');
        if (metaHeader) {
            return metaHeader.getAttribute('content');
        }
        return 'X-CSRF-TOKEN';
    }

    const badge = document.getElementById("badge-count");
    const list = document.getElementById("notification-list");
    const title = document.getElementById("notification-title");
    const campana = document.getElementById("campanaNotificaciones");

    if (!campana)
        return;

    function cargarNotificaciones() {
        fetch("/notificaciones/datos-completos")
                .then(res => res.json())
                .then(datos => {
                    const notificaciones = datos.notificaciones;
                    const totalNoLeidas = datos.totalNoLeidas;

                    list.querySelectorAll(".notification-item, hr, .dropdown-footer").forEach(e => e.remove());

                    if (notificaciones.length === 0) {
                        title.textContent = "No hay notificaciones";
                        badge.style.display = "none";
                        return;
                    }

                    notificaciones.forEach(n => {
                        const li = document.createElement("li");
                        li.classList.add("dropdown-item");

                        let iconClass = "";
                        let iconColor = "";

                        if (n.mensaje && n.mensaje.includes("venció")) {
                            iconClass = "bi bi-x-circle";
                            iconColor = "text-danger";
                        } else if (n.mensaje && n.mensaje.includes("vence")) {
                            iconClass = "bi bi-exclamation-circle";
                            iconColor = "text-warning";
                        } else {
                            iconClass = "bi bi-info-circle";
                            iconColor = "text-primary";
                        }

                        if (n.leida) {
                            li.style.opacity = "0.5";
                        }

                        li.innerHTML = `
                        <i class="${iconClass} ${iconColor}"></i>
                        <div>
                            <h4>${n.titulo || "Notificación"}</h4>
                            <p>${n.mensaje || "Sin mensaje"}</p>
                            <p><small>${n.fechaCreacion ? new Date(n.fechaCreacion).toLocaleString() : 'Fecha no disponible'}</small></p>
                        </div>
                    `;

                        li.addEventListener("click", () => {
                            if (n.idNotificacion) {
                                marcarNotificacionComoLeida(n.idNotificacion);
                            }
                        });

                        list.appendChild(li);
                        list.appendChild(document.createElement("hr")).classList.add("dropdown-divider");
                    });

                    actualizarBadgeYTitle(totalNoLeidas);

                    const footer = document.createElement("li");
                    footer.classList.add("dropdown-footer");
                    footer.innerHTML = `<a href="/notificaciones/todas">Mostrar todas las notificaciones</a>`;
                    list.appendChild(footer);
                })
                .catch(error => {
                    console.error("Error al cargar notificaciones:", error);
                    badge.style.display = "none";
                    title.textContent = "Error al cargar notificaciones";
                });
    }

    function marcarNotificacionComoLeida(idNotificacion) {
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        if (!csrfToken) {
            alert("Error de seguridad. Por favor, recarga la página.");
            return;
        }

        fetch(`/notificaciones/marcar-leida/${idNotificacion}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken  
            }
        })
                .then(response => {
                    if (response.ok) {
                        cargarNotificaciones();
                    } else if (response.status === 403) {
                        alert("No tienes permiso para realizar esta acción");
                    }
                })
                .catch(error => {
                    console.error("Error al marcar notificación:", error);
                    alert("Error al marcar notificación como leída");
                });
    }

    function marcarTodasComoLeidas() {
        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();

        if (!csrfToken) {
            alert("Error de seguridad. Por favor, recarga la página.");
            return;
        }

        fetch("/notificaciones/marcar-leidas", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken 
            }
        })
                .then(response => {
                    if (response.ok) {
                        badge.style.display = "none";
                        title.textContent = "No hay notificaciones nuevas";

                        document.querySelectorAll('.dropdown-item').forEach(item => {
                            item.style.opacity = "0.5";
                        });

                        setTimeout(() => {
                            cargarNotificaciones();
                        }, 500);
                    } else if (response.status === 403) {
                        alert("No tienes permiso para realizar esta acción");
                    }
                })
                .catch(error => {
                    console.error("Error al marcar todas como leídas:", error);
                    alert("Error al marcar notificaciones como leídas");
                });
    }

  
    function actualizarBadgeYTitle(totalNoLeidas) {
        if (totalNoLeidas > 0) {
            badge.textContent = totalNoLeidas;
            badge.style.display = "inline-block";
            title.textContent = `Tienes ${totalNoLeidas} notificación(es) nueva(s)`;
        } else {
            badge.style.display = "none";
            title.textContent = "No hay notificaciones nuevas";
        }
    }

    campana.addEventListener("click", (event) => {
        event.preventDefault();

        if (badge.style.display !== "none" && parseInt(badge.textContent) > 0) {
            marcarTodasComoLeidas();
            cargarNotificaciones();
        }


//        const dropdown = document.querySelector('.dropdown-menu');
//        if (dropdown) {
//            dropdown.classList.toggle('show');
//        }
    });

    list.addEventListener('click', (event) => {
        event.stopPropagation();
    });

    cargarNotificaciones();

    setInterval(cargarNotificaciones, 30000);

    
    

});