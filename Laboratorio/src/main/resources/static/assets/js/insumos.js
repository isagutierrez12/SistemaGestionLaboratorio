document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInsumoInput");
    const tbody = document.getElementById("tbodyInsumos");

    if (!input || !tbody) return;

    input.addEventListener("input", function () {
        const query = input.value.trim();

        tbody.innerHTML = `
            <tr>
                <td colspan="12" class="text-center text-muted">Buscando...</td>
            </tr>
        `;

        fetch(`/insumo/buscarJSON?query=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) throw new Error("Error al buscar insumos");
                return response.json();
            })
            .then(data => {
                tbody.innerHTML = "";

                if (data.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="12" class="text-center text-muted">
                                No se encontraron insumos
                            </td>
                        </tr>
                    `;
                    return;
                }

                data.forEach(i => {
                    const fila = document.createElement("tr");
                    fila.innerHTML = `
                        <td style="text-align:center;">${i.nombre}</td>
                        <td style="text-align:center;">${i.tipo}</td>
                        <td style="text-align:center;">${i.cantidadPorUnidad}</td>
                        <td style="text-align:center;">${i.unidadMedida}</td>
                        <td style="text-align:center;">${i.activo ? "Activo" : "Inactivo"}</td>

                        <td style="text-align:center;">
                            <a href="/insumo/modificar/${i.idInsumo}" class="btn-edit" title="Editar">
                                <i class="bi bi-pencil"></i>
                            </a>
                        </td>
                    `;
                    tbody.appendChild(fila);
                });
            })
            .catch(err => {
                console.error("Error búsqueda insumos:", err);
                tbody.innerHTML = `
                    <tr>
                        <td colspan="12" class="text-center text-danger">Error al cargar resultados</td>
                    </tr>
                `;
            });
    });
});
