document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInput");
    const tableBody = document.querySelector("table.datatable tbody");

    if (!input || !tableBody) return;

    input.addEventListener("input", function () {
        const query = input.value.trim();

        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted">Buscando...</td>
            </tr>
        `;

        fetch(`/paquete/buscar/json?query=${encodeURIComponent(query)}`)
            .then((response) => {
                if (!response.ok) throw new Error("Error al buscar paquetes");
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
});
