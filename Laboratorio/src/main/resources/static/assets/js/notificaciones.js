document.addEventListener("DOMContentLoaded", () => {
  const badge = document.getElementById("badge-count");
  const list = document.getElementById("notification-list");
  const title = document.getElementById("notification-title");

  fetch("/notificaciones/vencimientos")
    .then(res => res.json())
    .then(notificaciones => {
      // Limpiar lista actual
     

      if (notificaciones.length === 0) {
        title.textContent = "No hay notificaciones nuevas";
        badge.textContent = "0";
        return;
      }

      // Actualizar número de notificaciones
      badge.textContent = notificaciones.length;
      title.textContent = `Tienes ${notificaciones.length} notificación(es) reciente(s)`;

      notificaciones.forEach(n => {
        const li = document.createElement("li");
        li.classList.add("notification-item");

        let iconClass = "";
        let iconColor = "";
        if (n.mensaje.includes("venció")) {
          
          iconClass = "bi bi-x-circle";
          iconColor = "text-danger";
        } else if (n.mensaje.includes("vence")) {
        
          iconClass = "bi bi-exclamation-circle";
          iconColor = "text-warning";
        } else {
          iconClass = "bi bi-info-circle";
          iconColor = "text-primary";
        }

        li.innerHTML = `
          <i class="${iconClass} ${iconColor}"></i>
          <div>
            <h4>${n.titulo || "Notificación"}</h4>
            <p>${n.mensaje}</p>
            <p><small>${new Date(n.fechaCreacion).toLocaleString()}</small></p>
          </div>
        `;

        list.appendChild(li);
        list.appendChild(document.createElement("hr")).classList.add("dropdown-divider");
      });

      const footer = document.createElement("li");
      footer.classList.add("dropdown-footer");
      footer.innerHTML = `<a href="#">Mostrar todas las notificaciones</a>`;
      list.appendChild(footer);
    })
    .catch(err => {
      console.error("Error al cargar notificaciones:", err);
      title.textContent = "Error al cargar notificaciones";
    });
    const dropdownBell = document.querySelector('.nav-link.nav-icon[data-bs-toggle="dropdown"]');
  dropdownBell.addEventListener('show.bs.dropdown', () => {
    console.log("Campanita abierta — marcando como leídas...");

    fetch("/notificaciones/marcar-leidas", {
      method: "POST",
      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }).then(res => {
      if (res.ok) {
        console.log("Notificaciones marcadas como leídas");
        badge.textContent = "0";
      } else {
        console.error("Error al marcar notificaciones como leídas:", res.status);
      }
    }).catch(err => console.error("⚠️ Error al conectar con el servidor:", err));
  });
});

