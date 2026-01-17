document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formEditarCita");
    if (!form)
        return;

    const estadoSelect = document.getElementById("estado");
    let estadoOriginal = (document.getElementById("estadoOriginal")?.value || "").toUpperCase();

    let allowSubmit = false;

    function calcularNuevoMonto() {
        let total = 0;

        document.querySelectorAll('input[name="examenesSeleccionados"]:checked')
                .forEach(cb => total += parseFloat(cb.dataset.precio || "0"));

        document.querySelectorAll('input[name="paquetesSeleccionados"]:checked')
                .forEach(cb => total += parseFloat(cb.dataset.precio || "0"));

        return Math.round(total * 100) / 100;
    }

    form.addEventListener("submit", async (e) => {
    console.log("[cita-pago] submit fired. allowSubmit:", allowSubmit);
        if (allowSubmit)
            return;

        const estadoNuevo = (estadoSelect?.value || "").toUpperCase();
        const requierePago = (estadoNuevo === "CONFIRMADA");

        if (!requierePago)
            return;
        
        e.preventDefault();

        const totalCita = calcularNuevoMonto();

        const idCita = form.querySelector('input[name="idCita"]')?.value;

        // 1. Consultar si ya existe pago para esta cita
        let existePago = false;
        try {
            const resp = await fetch(`/cita/pago/existe?idCita=${encodeURIComponent(idCita)}`);
            existePago = resp.ok ? await resp.json() : false;
        } catch (err) {
            console.warn("[cita-pago] No se pudo validar si existe pago:", err);
        }

        // 2. Si existe, preguntar si desea sobreescribir
        if (existePago) {
            const confirm = await Swal.fire({
                icon: "warning",
                title: "Pago ya registrado",
                html: `
                        <p style="margin:0">
                          Ya existe un pago registrado para esta cita.<br>
                          ¿Desea <b>modificar</b> el pago anterior?
                        </p>
                      `,
                showCancelButton: true,
                confirmButtonText: "Sí, modificar",
                cancelButtonText: "No, cancelar",
                confirmButtonColor: "#1C94A4",
            });

            if (!confirm.isConfirmed) {
                return;
            }
        }

        const result = await Swal.fire({
            title: "Registrar pago",
            html: `
        <div class="pago-wrap">
          <label class="form-label">Monto pagado</label>

          <div class="form-check">
            <input class="form-check-input" type="radio" name="montoRadio" id="montoTotalRadio" checked>
            <label class="form-check-label" for="montoTotalRadio">
              Total de la cita (₡ ${totalCita.toFixed(2)})
            </label>
          </div>

          <div class="form-check">
            <input class="form-check-input" type="radio" name="montoRadio" id="montoOtroRadio">
            <label class="form-check-label" for="montoOtroRadio">Otro</label>
          </div>

          <input id="montoOtroInput" type="number" min="0" step="0.01"
                 class="swal2-input" placeholder="Ingrese monto" style="display:none;" />

          <label class="form-label" style="margin-top:.5rem;">Tipo de pago</label>
          <select id="tipoPagoSelect" class="swal2-select">
            <option value="">Seleccione...</option>
            <option value="Efectivo">Efectivo</option>
            <option value="Tarjeta">Tarjeta</option>
            <option value="SINPE">SINPE</option>
            <option value="SINPE y Tarjeta">SINPE y Tarjeta</option>
            <option value="SINPE y Efectivo">SINPE y Efectivo</option>
            <option value="Efectivo y Tarjeta">Efectivo y Tarjeta</option>
          </select>
        </div>
      `,
            showCancelButton: true,
            confirmButtonText: "Confirmar y actualizar",
            cancelButtonText: "Cancelar",
            width: 520,
            confirmButtonColor: "#1C94A4",
            didOpen: () => {
                const radioTotal = document.getElementById("montoTotalRadio");
                const radioOtro = document.getElementById("montoOtroRadio");
                const inputOtro = document.getElementById("montoOtroInput");

                const toggle = () => {
                    inputOtro.style.display = radioOtro.checked ? "block" : "none";
                    if (!radioOtro.checked)
                        inputOtro.value = "";
                };

                radioTotal.addEventListener("change", toggle);
                radioOtro.addEventListener("change", toggle);
                toggle();
            },
            preConfirm: () => {
                const radioOtro = document.getElementById("montoOtroRadio");
                const inputOtro = document.getElementById("montoOtroInput");
                const tipo = document.getElementById("tipoPagoSelect").value;

                let monto = totalCita;
                if (radioOtro.checked)
                    monto = parseFloat(inputOtro.value || "0");

                if (!tipo)
                    return Swal.showValidationMessage("Seleccione un tipo de pago.");
                if (!monto || monto <= 0)
                    return Swal.showValidationMessage("Ingrese un monto válido.");

                return {monto, tipo};
            }
        });

        if (!result.isConfirmed)
            return;

        document.getElementById("pagoMonto").value = Number(result.value.monto).toFixed(2);
        document.getElementById("pagoTipo").value = result.value.tipo;


        allowSubmit = true;
        form.requestSubmit();
    });
});

