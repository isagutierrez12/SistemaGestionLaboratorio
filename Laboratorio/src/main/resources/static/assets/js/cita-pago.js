document.addEventListener("DOMContentLoaded", () => {
  const form =
    document.querySelector('form[th\\:action="@{/cita/actualizar}"]') ||
    document.querySelector('form[action="/cita/actualizar"]');
  if (!form) return;

  const estadoSelect = document.getElementById("estado");
  const estadoOriginal = (document.getElementById("estadoOriginal")?.value || "").toUpperCase();

  function calcularNuevoMonto() {
    let precioTotal = 0;

    document
      .querySelectorAll('input[name="examenesSeleccionados"]:checked')
      .forEach(cb => {
        const precio = parseFloat(cb.dataset.precio || "0");
        precioTotal += precio;
      });

    document
      .querySelectorAll('input[name="paquetesSeleccionados"]:checked')
      .forEach(cb => {
        const examenesPaquete = JSON.parse(cb.dataset.examenes || "[]");
        examenesPaquete.forEach(precioExamen => {
          precioTotal += parseFloat(precioExamen || 0);
        });
      });

    return Math.round(precioTotal * 100) / 100;
  }



  form.addEventListener("submit", async (e) => {
    const estadoNuevo = (estadoSelect?.value || "").toUpperCase();
    const pasaATerminada = estadoOriginal !== "TERMINADA" && estadoNuevo === "TERMINADA";

    if (!pasaATerminada) return;

    e.preventDefault();

    const totalCita = calcularNuevoMonto();

    const html = `
      <div style="text-align:left">
        <label class="form-label" style="margin-bottom:.25rem">Monto pagado</label>

        <div style="margin-bottom:.75rem">
          <div class="form-check" style="margin-bottom:.25rem">
            <input class="form-check-input" type="radio" name="montoRadio" id="montoTotalRadio" checked>
            <label class="form-check-label" for="montoTotalRadio">
              Total de la cita (₡ ${totalCita.toFixed(2)})
            </label>
          </div>

          <div class="form-check" style="margin-bottom:.25rem">
            <input class="form-check-input" type="radio" name="montoRadio" id="montoOtroRadio">
            <label class="form-check-label" for="montoOtroRadio">
              Otro
            </label>
          </div>

          <input id="montoOtroInput" type="number" min="0" step="0.01"
                 class="swal2-input" placeholder="Ingrese monto"
                 style="display:none; width:100%; box-sizing:border-box; margin-top:.5rem" />
        </div>

        <label class="form-label" style="margin-bottom:.25rem">Tipo de pago</label>
        <select id="tipoPagoSelect" class="swal2-select" style="width:100%">
          <option value="">Seleccione...</option>
          <option value="Efectivo">Efectivo</option>
          <option value="Tarjeta">Tarjeta</option>
          <option value="SINPE">SINPE</option>
        </select>
      </div>
    `;

    const result = await Swal.fire({
      title: "Registrar pago",
      html,
      showCancelButton: true,
      confirmButtonText: "Confirmar y actualizar",
      cancelButtonText: "Cancelar",
      focusConfirm: false,
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
        if (radioOtro.checked) {
          monto = parseFloat(inputOtro.value || "0");
        }

        if (!tipo) {
          Swal.showValidationMessage("Seleccione un tipo de pago.");
          return false;
        }
        if (!monto || monto <= 0) {
          Swal.showValidationMessage("Ingrese un monto válido.");
          return false;
        }

        return { monto, tipo };
      }
    });

    if (!result.isConfirmed) return;

    document.getElementById("pagoMonto").value = result.value.monto;
    document.getElementById("pagoTipo").value = result.value.tipo;

    form.submit();
  });
});

