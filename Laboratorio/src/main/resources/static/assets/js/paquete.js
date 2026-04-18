document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInput");
    const tableBody = document.querySelector("table.datatable tbody");

    if (input && tableBody) {
        input.addEventListener("input", function () {
            const query = input.value.trim();

            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted">Buscando...</td>
                </tr>
            `;

            fetch(`/paquete/buscar/json?query=${encodeURIComponent(query)}`)
                    .then((response) => {
                        if (!response.ok)
                            throw new Error("Error al buscar paquetes");
                        return response.json();
                    })
                    .then((data) => {
                        tableBody.innerHTML = "";

                        if (data.length === 0) {
                            tableBody.innerHTML = `
                            <tr>
                                <td colspan="6" class="text-center text-muted">No se encontraron paquetes</td>
                            </tr>
                        `;
                            return;
                        }

                        data.forEach((p) => {
                            const tr = document.createElement("tr");
                            tr.innerHTML = `
                            <td>${p.codigo}</td>
                            <td style="vertical-align: middle; text-align: center;">${p.nombre}</td>
                            <td style="vertical-align: middle; text-align: center;">${p.descripcion}</td>
                            <td style="vertical-align: middle; text-align: center;">${p.precio}</td>
                            <td style="vertical-align: middle; text-align: center;">
                                ${p.activo ? "Activo" : "Inactivo"}
                            </td>
                            <td class="actions-cell" style="vertical-align: middle; text-align: center;">
                                <a href="/paquete/modificar/${p.idPaquete}" class="btn-edit" title="Editar">
                                    <i class="bi bi-pencil"></i>
                                </a>
                            </td>
                        `;
                            tableBody.appendChild(tr);
                        });
                    })
                    .catch((error) => {
                        console.error("Error en búsqueda dinámica:", error);
                        tableBody.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center text-danger">Error al cargar resultados</td>
                        </tr>
                    `;
                    });
        });
    }


    // ============ Edicion local de examenes en el paquete ==================
    const formEditarPaquete = document.getElementById("formEditarPaquete");
    const selectExamen = document.getElementById("selectExamenPaquete");
    const btnAgregarExamen = document.getElementById("btnAgregarExamen");
    const hiddenExamenes = document.getElementById("examenesSeleccionados");
    const tbodyExamenes = document.getElementById("tbodyExamenesPaquete");

    if (formEditarPaquete && selectExamen && btnAgregarExamen && hiddenExamenes && tbodyExamenes) {
        let examenes = [];

        function cargarExamenesDesdeTabla() {
            examenes = [];

            const filas = tbodyExamenes.querySelectorAll("tr[data-id]");
            filas.forEach((fila) => {
                const celdas = fila.querySelectorAll("td");
                examenes.push({
                    id: fila.dataset.id,
                    nombre: celdas[0] ? celdas[0].textContent.trim() : "",
                    area: celdas[1] ? celdas[1].textContent.trim() : "",
                    precio: celdas[2] ? celdas[2].textContent.trim() : ""
                });
            });

            actualizarHidden();
        }

        function actualizarHidden() {
            hiddenExamenes.value = examenes.map(ex => ex.id).join(",");
        }

        function renderTabla() {
            tbodyExamenes.innerHTML = "";

            if (examenes.length === 0) {
                tbodyExamenes.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted">No hay exámenes agregados aún</td>
                </tr>
            `;
                actualizarHidden();
                return;
            }

            examenes.forEach((ex) => {
                const tr = document.createElement("tr");
                tr.setAttribute("data-id", ex.id);
                tr.innerHTML = `
                <td>${ex.nombre}</td>
                <td>${ex.area}</td>
                <td>${ex.precio}</td>
                <td style="text-align:center;">
                    <button type="button"
                            class="btn btn-danger btn-sm btnQuitarExamen"
                            data-id="${ex.id}">
                        <i class="bi bi-x"></i> Quitar
                    </button>
                </td>
            `;
                tbodyExamenes.appendChild(tr);
            });

            actualizarHidden();
        }

        cargarExamenesDesdeTabla();

        btnAgregarExamen.addEventListener("click", function () {
            const id = selectExamen.value;

            if (!id) {
                selectExamen.reportValidity();
                return;
            }

            const option = selectExamen.options[selectExamen.selectedIndex];

            const yaExiste = examenes.some(ex => String(ex.id) === String(id));
            if (yaExiste) {
                Swal.fire({
                    title: "Atención",
                    text: "Ese examen ya está agregado al paquete.",
                    icon: "warning",
                    confirmButtonColor: "#1c94a4"
                });
                return;
            }

            examenes.push({
                id: id,
                nombre: option.dataset.nombre || option.textContent.trim(),
                area: option.dataset.area || "",
                precio: option.dataset.precio || ""
            });

            renderTabla();
            selectExamen.value = "";
        });

        tbodyExamenes.addEventListener("click", function (e) {
            const btn = e.target.closest(".btnQuitarExamen");
            if (!btn)
                return;

            const id = btn.dataset.id;
            examenes = examenes.filter(ex => String(ex.id) !== String(id));
            renderTabla();
        });

        formEditarPaquete.addEventListener("submit", function () {
            actualizarHidden();
        });
    }
});
