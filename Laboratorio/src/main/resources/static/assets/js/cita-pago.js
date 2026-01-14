document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("formEditarCita");
  if (!form) return;

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
    if (allowSubmit) return;

    const estadoNuevo = (estadoSelect?.value || "").toUpperCase();
    const pasaAConfirmada = estadoOriginal !== "CONFIRMADA" && estadoNuevo === "CONFIRMADA";
    if (!pasaAConfirmada) return;

    e.preventDefault();

    const totalCita = calcularNuevoMonto();

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
          </select>
        </div>
      `,
      showCancelButton: true,
      confirmButtonText: "Confirmar y actualizar",
      cancelButtonText: "Cancelar",
      width: 520,
      confirmButtonColor: "#1C94A4", // <-- botón azul del sistema
      didOpen: () => {
        const radioTotal = document.getElementById("montoTotalRadio");
        const radioOtro = document.getElementById("montoOtroRadio");
        const inputOtro = document.getElementById("montoOtroInput");

        const toggle = () => {
          inputOtro.style.display = radioOtro.checked ? "block" : "none";
          if (!radioOtro.checked) inputOtro.value = "";
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
        if (radioOtro.checked) monto = parseFloat(inputOtro.value || "0");

        if (!tipo) return Swal.showValidationMessage("Seleccione un tipo de pago.");
        if (!monto || monto <= 0) return Swal.showValidationMessage("Ingrese un monto válido.");

        return { monto, tipo };
      }
    });

    if (!result.isConfirmed) return;

    document.getElementById("pagoMonto").value = Number(result.value.monto).toFixed(2);
    document.getElementById("pagoTipo").value = result.value.tipo;

    estadoOriginal = "CONFIRMADA";
    document.getElementById("estadoOriginal").value = "CONFIRMADA";

    allowSubmit = true;
    form.requestSubmit();
  });
});
