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
