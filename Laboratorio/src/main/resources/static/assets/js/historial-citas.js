document.addEventListener("DOMContentLoaded", function () {
  const botonesDetalle = document.querySelectorAll(".btn-detalle-cita");

  if (!botonesDetalle || botonesDetalle.length === 0) return;

  botonesDetalle.forEach((btn) => {
    btn.addEventListener("click", function () {
      const idCita = this.getAttribute("data-id");
      if (!idCita) return;

      fetch(`/cita/detalle/${idCita}`)
  .then(async (response) => {
    const text = await response.text();
    console.log("STATUS:", response.status);
    console.log("RESPUESTA CRUDA:", text);

    if (!response.ok) {
      throw new Error("Error en la respuesta del servidor");
    }

    return JSON.parse(text);
  })
        .then((data) => {
          // Si el backend devolvió un string
          if (typeof data === "string") {
            Swal.fire({
              title: "Error",
              text: data,
              icon: "error",
              confirmButtonText: "Aceptar",
              confirmButtonColor: "#1c94a4",
            });
            return;
          }

          const examenes = data.examenes || [];
          const paquetes = data.paquetes || [];

          let html = `<div style="text-align:left;">`;

          if (paquetes.length > 0) {
            html += "<h5>Paquetes</h5><ul>";
            paquetes.forEach((p) => {
              html += `<li><b>${p.codigo}</b> - ${p.nombre}</li>`;
            });
            html += "</ul><hr/>";
          }

          if (examenes.length > 0) {
            html += "<h5>Exámenes</h5><ul>";
            examenes.forEach((e) => {
              html += `<li><b>${e.codigo}</b> - ${e.nombre}`;
              if (e.extra) {
                html += `<br/><small><i>Condiciones: ${e.extra}</i></small>`;
              }
              html += "</li>";
            });
            html += "</ul>";
          } else {
            html += "<p><i>No se registraron exámenes en esta cita.</i></p>";
          }

          Swal.fire({
            title: "Detalle de la cita",
            html: html,
            confirmButtonText: "Cerrar",
            confirmButtonColor: "#1c94a4",
            width: "600px",
          });
        })
        .catch((error) => {
          console.error("Error al obtener detalle de la cita:", error);
          Swal.fire({
            title: "Error",
            text: "Error al cargar los detalles de la cita.",
            icon: "error",
            confirmButtonText: "Aceptar",
            confirmButtonColor: "#1c94a4",
          });
        });
    });
  });
});
