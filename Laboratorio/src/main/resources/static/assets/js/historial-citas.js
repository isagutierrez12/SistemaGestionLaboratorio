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

document.addEventListener("DOMContentLoaded", function () {

    const FILAS_POR_PAGINA = 10; 

    const tabla = document.querySelector("table");
    const tbody = tabla?.querySelector("tbody");

    if (!tbody) return;

    let filas = Array.from(tbody.querySelectorAll("tr"));

    if (filas.length <= FILAS_POR_PAGINA) return;


    const wrapper = document.createElement("div");
    wrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    wrapper.appendChild(nav);

    tabla.parentElement.appendChild(wrapper);

    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        const inicio = (pagina - 1) * FILAS_POR_PAGINA;
        const fin = inicio + FILAS_POR_PAGINA;

        filas.forEach((fila, index) => {
            fila.style.display =
                index >= inicio && index < fin ? "" : "none";
        });

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(filas.length / FILAS_POR_PAGINA);

        ul.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

        for (let i = 1; i <= totalPaginas; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual) li.classList.add("active");
            ul.appendChild(li);
        }

        ul.appendChild(
            crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas)
        );
    }

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.href = "#";
        a.className = "page-link";
        a.textContent = texto;

        a.style.color = "#333";
        a.style.backgroundColor = "#fff";
        a.style.borderColor = "#ccc";

        a.addEventListener("mouseenter", () => {
            if (!li.classList.contains("active")) {
                a.style.backgroundColor = "#f2f2f2";
            }
        });

        a.addEventListener("mouseleave", () => {
            if (!li.classList.contains("active")) {
                a.style.backgroundColor = "#fff";
            }
        });

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    mostrarPagina(1);
});

