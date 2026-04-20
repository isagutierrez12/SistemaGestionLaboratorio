document.addEventListener("DOMContentLoaded", function () {
    const btnContinuar = document.getElementById("btnContinuar");
    const btnSeleccionarTodo = document.getElementById("btnSeleccionarTodo");
    const btnDeseleccionarTodo = document.getElementById("btnDeseleccionarTodo");
    const inputBusqueda = document.getElementById("busquedaProforma");
    const tbodyProforma = document.getElementById("tbodyProforma");

    if (!tbodyProforma) return;

    const filas = Array.from(tbodyProforma.querySelectorAll("tr"));
    const filasPorPagina = 15;
    let paginaActual = 1;
    let filasFiltradas = [...filas];

    const paginacionWrapper = document.createElement("div");
    paginacionWrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    paginacionWrapper.appendChild(nav);

    const tabla = tbodyProforma.closest("table");
    if (tabla && tabla.parentNode) {
        tabla.parentNode.appendChild(paginacionWrapper);
    }

    function obtenerChecks() {
        return document.querySelectorAll(".exam-check");
    }

    function actualizarBoton() {
        const checks = obtenerChecks();
        const algunoSeleccionado = Array.from(checks).some(ch => ch.checked);

        if (btnContinuar) {
            btnContinuar.disabled = !algunoSeleccionado;
        }
    }

    function aplicarEstiloCheck(check) {
        if (check.checked) {
            check.style.backgroundColor = "#1c94a4";
            check.style.borderColor = "#1c94a4";
        } else {
            check.style.backgroundColor = "transparent";
            check.style.borderColor = "#1c94a4";
        }
    }

    function sincronizarEstilosChecks() {
        const checks = obtenerChecks();
        checks.forEach(aplicarEstiloCheck);
    }

    function bindChecks() {
        const checks = obtenerChecks();
        checks.forEach(ch => {
            ch.removeEventListener("change", manejarCambioCheck);
            ch.addEventListener("change", manejarCambioCheck);
        });
    }

    function manejarCambioCheck(e) {
        aplicarEstiloCheck(e.target);
        actualizarBoton();
    }

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        filas.forEach(fila => fila.style.display = "none");

        const inicio = (pagina - 1) * filasPorPagina;
        const fin = inicio + filasPorPagina;

        filasFiltradas.slice(inicio, fin).forEach(fila => {
            fila.style.display = "";
        });

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(filasFiltradas.length / filasPorPagina);

        if (totalPaginas <= 1) {
            return;
        }

        ul.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

        for (let i = 1; i <= totalPaginas; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual) {
                li.classList.add("active");
            }
            ul.appendChild(li);
        }

        ul.appendChild(crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas));
    }

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.className = "page-link";
        a.href = "#";
        a.textContent = texto;

        a.addEventListener("click", function (e) {
            e.preventDefault();
            if (!deshabilitado) {
                mostrarPagina(pagina);
            }
        });

        li.appendChild(a);
        return li;
    }

    function aplicarFiltro() {
        const texto = inputBusqueda ? inputBusqueda.value.toLowerCase().trim() : "";

        filasFiltradas = filas.filter(fila =>
            fila.textContent.toLowerCase().includes(texto)
        );

        paginaActual = 1;
        mostrarPagina(paginaActual);
    }

    if (inputBusqueda) {
        inputBusqueda.addEventListener("input", aplicarFiltro);
    }

    if (btnSeleccionarTodo) {
        btnSeleccionarTodo.addEventListener("click", function () {
            const checks = obtenerChecks();
            checks.forEach(ch => {
                ch.checked = true;
                aplicarEstiloCheck(ch);
            });
            actualizarBoton();
        });
    }

    if (btnDeseleccionarTodo) {
        btnDeseleccionarTodo.addEventListener("click", function () {
            const checks = obtenerChecks();
            checks.forEach(ch => {
                ch.checked = false;
                aplicarEstiloCheck(ch);
            });
            actualizarBoton();
        });
    }

    if (btnContinuar) {
        btnContinuar.addEventListener("click", function (e) {
            const seleccionados = document.querySelectorAll(".exam-check:checked").length;

            if (seleccionados === 0) {
                e.preventDefault();
                alert("Debe seleccionar al menos un examen");
            }
        });
    }

    bindChecks();
    sincronizarEstilosChecks();
    actualizarBoton();
    mostrarPagina(1);
});
