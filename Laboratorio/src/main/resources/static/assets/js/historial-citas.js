document.addEventListener("DOMContentLoaded", function () {
  const cards = document.querySelectorAll(".cita-card");
  if (!cards || cards.length === 0) return;

  cards.forEach((card) => {
    const idCita = card.getAttribute("data-id");
    if (!idCita) return;

    const ulPaquetes = card.querySelector(".paquetes-container");
    const ulExamenes = card.querySelector(".examenes-container");

    fetch(`/cita/detalle/${idCita}`)
      .then((response) => {
        if (!response.ok) throw new Error("Error en la respuesta del servidor");
        return response.json();
      })
      .then((data) => {
        // Si el backend devolvió un string de error
        if (typeof data === "string") {
          ulPaquetes.innerHTML = `<li class="list-group-item text-danger small">${data}</li>`;
          ulExamenes.innerHTML = `<li class="list-group-item text-danger small">${data}</li>`;
          return;
        }

        const examenes = data.examenes || [];
        const paquetes = data.paquetes || [];

        // Paquetes
        if (paquetes.length > 0) {
          ulPaquetes.innerHTML = paquetes
            .map(
              (p) =>
                `<li class="list-group-item">
                   <span class="fw-semibold">${p.codigo}</span> - ${p.nombre}
                 </li>`
            )
            .join("");
        } else {
          ulPaquetes.innerHTML =
            `<li class="list-group-item text-muted small"><i>No se registraron paquetes.</i></li>`;
        }

        // Exámenes
        if (examenes.length > 0) {
          ulExamenes.innerHTML = examenes
            .map((e) => {
              const extra = e.extra
                ? `<div class="small text-muted"><i>Condiciones: ${e.extra}</i></div>`
                : "";
              return `<li class="list-group-item">
                        <span class="fw-semibold">${e.codigo}</span> - ${e.nombre}
                        ${extra}
                      </li>`;
            })
            .join("");
        } else {
          ulExamenes.innerHTML =
            `<li class="list-group-item text-muted small"><i>No se registraron exámenes.</i></li>`;
        }
      })
      .catch((error) => {
        console.error("Error al obtener detalle de la cita:", error);
        ulPaquetes.innerHTML =
          `<li class="list-group-item text-danger small">Error al cargar paquetes.</li>`;
        ulExamenes.innerHTML =
          `<li class="list-group-item text-danger small">Error al cargar exámenes.</li>`;
      });
  });
});

