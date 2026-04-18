document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 6;

    const contenedor = document.getElementById("contenedorCriticos");
    const paginacion = document.querySelector(".pagination");

    const filtroUsuario = document.getElementById("filtroUsuarioCrit");
    const filtroTipoEvento = document.getElementById("filtroTipoEventoCrit");

    const fechaInicio = document.getElementById("fechaInicioCrit");
    const fechaFin = document.getElementById("fechaFinCrit");

    const btnRestaurar = document.getElementById("btnRestaurarCrit");

    if (!contenedor || !paginacion)
        return;

    const todos = Array.from(contenedor.querySelectorAll(".test"));

    let filtrados = [...todos];
    let paginaActual = 1;

    function parseFecha(texto) {
        const match = texto.match(/(\d{2})\/(\d{2})\/(\d{4})/);
        if (!match)
            return null;
        return new Date(match[3], match[2] - 1, match[1]);
    }

    function mostrarPagina(pagina) {
        paginaActual = pagina;
        contenedor.innerHTML = "";

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        const paginaItems = filtrados.slice(inicio, fin);

        if (paginaItems.length === 0) {
            contenedor.innerHTML = `
                <div class="text-center text-muted p-3">
                    No se encontraron auditorías críticas
                </div>`;
            renderizarPaginacion();
            return;
        }

        paginaItems.forEach(item => contenedor.appendChild(item));
        renderizarPaginacion();
    }

    // PAGINACIÓN
    function renderizarPaginacion() {
        paginacion.innerHTML = "";

        const totalPaginas = Math.ceil(filtrados.length / ITEMS_POR_PAGINA);

        if (totalPaginas <= 1)
            return;

        const VENTANA = 10;

        let inicio = Math.floor((paginaActual - 1) / VENTANA) * VENTANA + 1;
        let fin = Math.min(inicio + VENTANA - 1, totalPaginas);

        // Botón Anterior
        paginacion.appendChild(
                crearBoton("Anterior", paginaActual - 1, paginaActual === 1)
                );

        // Ir a primera página si no estamos en el inicio
        if (inicio > 1) {
            paginacion.appendChild(crearBoton("1", 1));
            paginacion.appendChild(crearEllipsis());
        }

        // Ventana de páginas
        for (let i = inicio; i <= fin; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual)
                li.classList.add("active");
            paginacion.appendChild(li);
        }

        // Ir a última página si no estamos al final
        if (fin < totalPaginas) {
            paginacion.appendChild(crearEllipsis());
            paginacion.appendChild(crearBoton(totalPaginas, totalPaginas));
        }

        // Botón Siguiente
        paginacion.appendChild(
                crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas)
                );
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

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.classList.add("page-item");

        if (deshabilitado)
            li.classList.add("disabled");

        const a = document.createElement("a");
        a.classList.add("page-link");
        a.href = "#";
        a.textContent = texto;

        a.addEventListener("click", (e) => {
            e.preventDefault();
            if (!deshabilitado)
                mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    // FILTROS (TODO UNIFICADO)
    function aplicarFiltros() {

        const usuario = filtroUsuario.value.trim().toLowerCase();
        const tipoEvento = filtroTipoEvento.value.trim().toLowerCase();

        const inicio = fechaInicio.value ? new Date(fechaInicio.value) : null;
        const fin = fechaFin.value ? new Date(fechaFin.value) : null;

        filtrados = todos.filter(item => {

            const usuarioTexto =
                    item.querySelector(".info-row:nth-child(1)").textContent.toLowerCase();

            const tipoEventoTexto =
                    item.querySelector(".test-header h3").textContent.toLowerCase();

            const fechaTexto =
                    item.querySelector(".info-row:nth-child(3)")?.textContent || "";

            const fecha = parseFecha(fechaTexto);

            const cumpleTexto =
                    (!usuario || usuarioTexto.includes(usuario)) &&
                    (!tipoEvento || tipoEventoTexto.includes(tipoEvento));

            const cumpleFecha =
                    (!inicio || (fecha && fecha >= inicio)) &&
                    (!fin || (fecha && fecha <= fin));

            return cumpleTexto && cumpleFecha;
        });

        mostrarPagina(1);
    }

    // RESTAURAR
    function restaurar() {

        filtroUsuario.value = "";
        filtroTipoEvento.value = "";
        fechaInicio.value = "";
        fechaFin.value = "";

        filtrados = [...todos];
        mostrarPagina(1);
    }

    [filtroUsuario, filtroTipoEvento].forEach(el => {
        el.addEventListener("input", aplicarFiltros);
    });

    if (fechaInicio)
        fechaInicio.addEventListener("change", aplicarFiltros);
    if (fechaFin)
        fechaFin.addEventListener("change", aplicarFiltros);

    if (btnRestaurar) {
        btnRestaurar.addEventListener("click", restaurar);
    }

    mostrarPagina(1);
});