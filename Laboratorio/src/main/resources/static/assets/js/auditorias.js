document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 6;

    const contenedor = document.getElementById("contenedorAuditorias");
    const paginacion = document.querySelector(".pagination");

    const filtroUsuario = document.getElementById("filtroUsuario");
    const filtroModulo = document.getElementById("filtroModulo");
    const filtroAccion = document.getElementById("filtroAccion");
    const btnRestaurar = document.getElementById("btnRestaurar");

    if (!contenedor || !paginacion) return;

    const todos = Array.from(contenedor.querySelectorAll(".test"));
    let filtrados = [...todos];
    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;
        contenedor.innerHTML = "";

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        const paginaItems = filtrados.slice(inicio, fin);

        if (paginaItems.length === 0) {
            contenedor.innerHTML = `
                <div class="text-center text-muted p-3">
                    No se encontraron auditorías
                </div>`;
            renderizarPaginacion();
            return;
        }

        paginaItems.forEach(item => contenedor.appendChild(item));
        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        paginacion.innerHTML = "";

        const totalPaginas = Math.ceil(filtrados.length / ITEMS_POR_PAGINA);
        if (totalPaginas <= 1) return;

        paginacion.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

        for (let i = 1; i <= totalPaginas; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual) li.classList.add("active");
            paginacion.appendChild(li);
        }

        paginacion.appendChild(crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas));
    }

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.classList.add("page-item", "pagination-neutral");

        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.classList.add("page-link");
        a.href = "#";
        a.textContent = texto;

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    function aplicarFiltros() {
        const usuario = filtroUsuario.value.trim().toLowerCase();
        const modulo = filtroModulo.value.trim().toLowerCase();
        const accion = filtroAccion.value.trim().toLowerCase();

        filtrados = todos.filter(aud => {
            const usuarioTexto = aud.querySelector(".info-row:nth-child(1)").textContent.toLowerCase();
            const moduloTexto = aud.querySelector(".test-header h3").textContent.toLowerCase();
            const accionTexto = aud.querySelector(".badge").textContent.toLowerCase();

            return (!usuario || usuarioTexto.includes(usuario)) &&
                   (!modulo || moduloTexto.includes(modulo)) &&
                   (!accion || accionTexto.includes(accion));
        });

        mostrarPagina(1);
    }

    function restaurar() {
        filtroUsuario.value = "";
        filtroModulo.value = "";
        filtroAccion.value = "";
        filtrados = [...todos];
        mostrarPagina(1);
    }

    [filtroUsuario, filtroModulo, filtroAccion].forEach(input => {
        input.addEventListener("input", aplicarFiltros);
    });

    if (btnRestaurar) {
        btnRestaurar.addEventListener("click", restaurar);
    }

    mostrarPagina(1);
});
