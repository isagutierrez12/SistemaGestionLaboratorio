document.addEventListener("DOMContentLoaded", function () {
    const filtroUsuario = document.getElementById("filtroUsuarioCrit");
    const filtroTipoEvento = document.getElementById("filtroTipoEventoCrit");
    const btnRestaurar = document.getElementById("btnRestaurarCrit");
    const contenedor = document.getElementById("contenedorCriticos");

    if (!contenedor) return;

    let criticosOriginales = Array.from(contenedor.querySelectorAll(".test"));

    function aplicarFiltros() {

        const usuario = filtroUsuario.value.trim().toLowerCase();
        const tipoEvento = filtroTipoEvento.value.trim().toLowerCase();

        contenedor.innerHTML = "";

        const filtradas = criticosOriginales.filter(aud => {
            const usuarioTexto = aud.querySelector(".test-info .info-row:nth-child(1)").textContent.toLowerCase();
            const tipoEventoTexto = aud.querySelector(".test-header h3").textContent.toLowerCase();

            return (!usuario || usuarioTexto.includes(usuario)) &&
                   (!tipoEvento || tipoEventoTexto.includes(tipoEvento));
        });

        if (filtradas.length === 0) {
            contenedor.innerHTML = `<div class="text-center text-muted p-3">No se encontraron auditorías críticas</div>`;
            return;
        }

        filtradas.forEach(aud => contenedor.appendChild(aud));
    }

    [filtroUsuario, filtroTipoEvento].forEach(input => {
        input.addEventListener("input", aplicarFiltros);
    });

    btnRestaurar.addEventListener("click", function () {
        filtroUsuario.value = "";
        filtroTipoEvento.value = "";

        contenedor.innerHTML = "";
        criticosOriginales.forEach(aud => contenedor.appendChild(aud));
    });
});
document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 6;
    const MAX_PAGINAS_VISIBLES = 10;

    const contenedor = document.getElementById("contenedorCriticos");
    const paginacion = document.querySelector(".pagination");

    if (!contenedor || !paginacion) return;

    const todos = Array.from(contenedor.querySelectorAll(".test"));
    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;
        contenedor.innerHTML = "";

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        todos.slice(inicio, fin).forEach(item => contenedor.appendChild(item));
        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        paginacion.innerHTML = "";

        const totalPaginas = Math.ceil(todos.length / ITEMS_POR_PAGINA);
        if (totalPaginas <= 1) return;

        paginacion.appendChild(
            crearBoton("Anterior", paginaActual - 1, paginaActual === 1)
        );

        let inicio = Math.max(1, paginaActual - Math.floor(MAX_PAGINAS_VISIBLES / 2));
        let fin = inicio + MAX_PAGINAS_VISIBLES - 1;

        if (fin > totalPaginas) {
            fin = totalPaginas;
            inicio = Math.max(1, fin - MAX_PAGINAS_VISIBLES + 1);
        }

        if (inicio > 1) {
            paginacion.appendChild(crearBoton(1, 1));
            paginacion.appendChild(crearEllipsis());
        }

        for (let i = inicio; i <= fin; i++) {
            const li = crearBoton(i, i);
            if (i === paginaActual) li.classList.add("active");
            paginacion.appendChild(li);
        }

        if (fin < totalPaginas) {
            paginacion.appendChild(crearEllipsis());
            paginacion.appendChild(crearBoton(totalPaginas, totalPaginas));
        }

        paginacion.appendChild(
            crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas)
        );
    }

    function crearBoton(texto, pagina, deshabilitado = false) {
        const li = document.createElement("li");
        li.className = "page-item pagination-neutral";
        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.className = "page-link";
        a.href = "#";
        a.textContent = texto;

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    function crearEllipsis() {
        const li = document.createElement("li");
        li.className = "page-item disabled";

        const span = document.createElement("span");
        span.className = "page-link";
        span.textContent = "...";

        li.appendChild(span);
        return li;
    }

    mostrarPagina(1);
});
