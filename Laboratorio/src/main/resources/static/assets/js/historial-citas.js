document.addEventListener("DOMContentLoaded", function () {
    const cards = document.querySelectorAll(".cita-card");
    if (!cards || cards.length === 0)
        return;

    cards.forEach((card) => {
        const idCita = card.getAttribute("data-id");
        if (!idCita)
            return;

        const ulPaquetes = card.querySelector(".paquetes-container");
        const ulExamenes = card.querySelector(".examenes-container");

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

document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 10;

    const contenedor = document.getElementById("contenedorCitas");
    if (!contenedor)
        return;

    const cards = Array.from(contenedor.querySelectorAll(".cita-card"));
    if (cards.length <= ITEMS_POR_PAGINA)
        return;

    const paginacion = document.querySelector(".pagination");
    if (!paginacion)
        return;

    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        cards.forEach((card, index) => {
            card.style.display =
                    index >= inicio && index < fin ? "" : "none";
        });

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        paginacion.innerHTML = "";

        const totalPaginas = Math.ceil(cards.length / ITEMS_POR_PAGINA);

        paginacion.appendChild(
                crearBoton("Anterior", paginaActual - 1, paginaActual === 1)
                );

        for (let i = 1; i <= totalPaginas; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual)
                li.classList.add("active");
            paginacion.appendChild(li);
        }

        paginacion.appendChild(
                crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas)
                );
    }

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (deshabilitado)
            li.classList.add("disabled");

        const a = document.createElement("a");
        a.href = "#";
        a.className = "page-link";
        a.textContent = texto;

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado)
                mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    mostrarPagina(1);
});

