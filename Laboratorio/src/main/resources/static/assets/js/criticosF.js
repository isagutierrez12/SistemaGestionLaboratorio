document.addEventListener("DOMContentLoaded", function () {

    const fechaInicio = document.getElementById("fechaInicioCrit");
    const fechaFin = document.getElementById("fechaFinCrit");
    const contenedor = document.getElementById("contenedorCriticos");

    if (!contenedor) return;

    let criticosOriginales = Array.from(contenedor.querySelectorAll(".test"));

    function filtrarPorFecha() {

        const inicio = fechaInicio.value ? new Date(fechaInicio.value) : null;
        const fin = fechaFin.value ? new Date(fechaFin.value) : null;

        contenedor.innerHTML = "";

        const filtradas = criticosOriginales.filter(aud => {

            const fechaTexto = aud.querySelector(".info-row:nth-child(3)").textContent;
            const partes = fechaTexto.match(/(\d{2})\/(\d{2})\/(\d{4})/);

            if (!partes) return true;

            const fechaAud = new Date(partes[3], partes[2] - 1, partes[1]);

            return (!inicio || fechaAud >= inicio) &&
                   (!fin || fechaAud <= fin);
        });

        if (filtradas.length === 0) {
            contenedor.innerHTML = `<div class="text-center text-muted p-3">No se encontraron auditorías críticas</div>`;
            return;
        }

        filtradas.forEach(aud => contenedor.appendChild(aud));
    }

    fechaInicio.addEventListener("change", filtrarPorFecha);
    fechaFin.addEventListener("change", filtrarPorFecha);
});
