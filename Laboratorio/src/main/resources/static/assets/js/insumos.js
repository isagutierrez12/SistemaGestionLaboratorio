document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInsumoInput");
    const tbody = document.getElementById("tbodyInsumos");

    if (!input || !tbody) return;

    input.addEventListener("input", function () {
        const query = input.value.trim();

        tbody.innerHTML = `
            <tr>
                <td colspan="12" class="text-center text-muted">Buscando...</td>
            </tr>
        `;

        fetch(`/insumo/buscarJSON?query=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) throw new Error("Error al buscar insumos");
                return response.json();
            })
            .then(data => {
                tbody.innerHTML = "";

                if (data.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="12" class="text-center text-muted">
                                No se encontraron insumos
                            </td>
                        </tr>
                    `;
                    return;
                }

                data.forEach(i => {
                    const fila = document.createElement("tr");
                    fila.innerHTML = `
                        <td style="text-align:center;">${i.nombre}</td>
                        <td style="text-align:center;">${i.tipo}</td>
                        <td style="text-align:center;">${i.cantidadPorUnidad}</td>
                        <td style="text-align:center;">${i.unidadMedida}</td>
                        <td style="text-align:center;">${i.activo ? "Activo" : "Inactivo"}</td>

                        <td style="text-align:center;">
                            <a href="/insumo/modificar/${i.idInsumo}" class="btn-edit" title="Editar">
                                <i class="bi bi-pencil"></i>
                            </a>
                        </td>
                    `;
                    tbody.appendChild(fila);
                });
            })
            .catch(err => {
                console.error("Error búsqueda insumos:", err);
                tbody.innerHTML = `
                    <tr>
                        <td colspan="12" class="text-center text-danger">Error al cargar resultados</td>
                    </tr>
                `;
            });
    });
});

document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 25;

    const tbody = document.getElementById("tbodyInsumos");
    if (!tbody) return;

    const filas = Array.from(tbody.querySelectorAll("tr"));

    if (filas.length <= ITEMS_POR_PAGINA) return;

    const paginacionWrapper = document.createElement("div");
    paginacionWrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    paginacionWrapper.appendChild(nav);

    tbody.closest(".card-body").appendChild(paginacionWrapper);

    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        filas.forEach((fila, index) => {
            fila.style.display =
                index >= inicio && index < fin ? "" : "none";
        });

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(filas.length / ITEMS_POR_PAGINA);

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

