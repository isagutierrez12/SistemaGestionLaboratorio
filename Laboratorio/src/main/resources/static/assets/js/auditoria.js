document.addEventListener("DOMContentLoaded", function () {
    const filtroUsuario = document.getElementById("filtroUsuario");
    const filtroModulo = document.getElementById("filtroModulo");
    const filtroAccion = document.getElementById("filtroAccion");
    const fechaInicio = document.getElementById("fechaInicio");
    const fechaFin = document.getElementById("fechaFin");
    const btnRestaurar = document.getElementById("btnRestaurar");
    const contenedor = document.getElementById("contenedorAuditorias");

    if (!contenedor) return;

    let auditoriasOriginales = Array.from(contenedor.querySelectorAll(".test"));

    function aplicarFiltros() {
        const usuario = filtroUsuario.value.trim().toLowerCase();
        const modulo = filtroModulo.value.trim().toLowerCase();
        const accion = filtroAccion.value.trim().toLowerCase();

        contenedor.innerHTML = "";

        const filtradas = auditoriasOriginales.filter(aud => {
            const usuarioTexto = aud.querySelector(".test-info .info-row:nth-child(1)").textContent.toLowerCase();
            const moduloTexto = aud.querySelector(".test-header h3").textContent.toLowerCase();
            const accionTexto = aud.querySelector(".test-header span.badge").textContent.toLowerCase();

            return (!usuario || usuarioTexto.includes(usuario)) &&
                   (!modulo || moduloTexto.includes(modulo)) &&
                   (!accion || accionTexto.includes(accion));
        });

        if (filtradas.length === 0) {
            contenedor.innerHTML = `<div class="text-center text-muted p-3">No se encontraron auditorías</div>`;
            return;
        }

        filtradas.forEach(aud => contenedor.appendChild(aud));
    }

    [filtroUsuario, filtroModulo, filtroAccion].forEach(input => {
        input.addEventListener("input", aplicarFiltros);
    });

    btnRestaurar.addEventListener("click", function () {
        filtroUsuario.value = "";
        filtroModulo.value = "";
        filtroAccion.value = "";

        if (fechaInicio) fechaInicio.value = "";
        if (fechaFin) fechaFin.value = "";

        contenedor.innerHTML = "";
        auditoriasOriginales.forEach(aud => contenedor.appendChild(aud));
    });
});
