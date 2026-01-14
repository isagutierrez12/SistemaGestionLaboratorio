document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInput");
    const catalog = document.querySelector(".catalog");

    if (!input || !catalog) return;

    input.addEventListener("input", function () {
        const query = input.value.trim();

        // Mensaje mientras busca
        catalog.innerHTML = `
            <div class="text-center text-muted mt-3">Buscando...</div>
        `;

        fetch(`/examen/buscar/json?query=${encodeURIComponent(query)}`)
            .then((response) => {
                if (!response.ok) throw new Error("Error al buscar exámenes");
                return response.json();
            })
            .then((data) => {
                catalog.innerHTML = "";

                if (data.length === 0) {
                    catalog.innerHTML = `
                        <div class="text-center text-muted mt-3">No se encontraron exámenes</div>
                    `;
                    return;
                }

                data.forEach((e) => {
                    const div = document.createElement("div");
                    div.classList.add("test");
                    div.innerHTML = `
                        <div class="test-header">
                            <span class="code">${e.codigo}</span>
                            <h3>${e.nombre}</h3>
                        </div>

                        <div class="test-body">
                            <div class="test-info">
                                <div class="info-row"><strong>Area:</strong> ${e.area}</div>
                                <div class="info-row"><strong>Condiciones:</strong> ${e.condiciones ?? ""}</div>
                                <div class="info-row"><strong>Precio:</strong> $${e.precio}</div>
                                <div class="info-row">
                                    <strong>Rango:</strong> ${e.valorMinimo} - ${e.valorMaximo} ${e.unidad}
                                </div>
                                <div class="info-row description">
                                    <strong>Estado:</strong> ${e.activo ? "Activo" : "Inactivo"}
                                </div>
                            </div>
                        </div>

                        <div class="test-footer">
                            <a href="/examen/modificar/${e.idExamen}" class="btn-exam" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </a>
                        </div>
                    `;
                    catalog.appendChild(div);
                });
            })
            .catch((error) => {
                console.error("Error en búsqueda dinámica:", error);
                catalog.innerHTML = `
                    <div class="text-center text-danger mt-3">Error al cargar resultados</div>
                `;
            });
    });
});

document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 6; 
    const catalog = document.querySelector(".catalog");

    if (!catalog) return;

    const cards = Array.from(catalog.querySelectorAll(".test"));

    if (cards.length <= ITEMS_POR_PAGINA) return;

    const paginacionWrapper = document.createElement("div");
    paginacionWrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    paginacionWrapper.appendChild(nav);

    catalog.parentElement.appendChild(paginacionWrapper);

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
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(cards.length / ITEMS_POR_PAGINA);

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
        a.textContent = texto;
        a.className = "page-link";

        a.style.color = "#000";
        a.style.backgroundColor = "#fff";
        a.style.borderColor = "#ccc";

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    mostrarPagina(1);
});
