document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInventarioInput");
    const tbody = document.getElementById("tbodyInventario");

    if (!tbody) return;

    const paginator = setupTablePaginator({
        tbody: tbody,
        pageSize: 25
    });

    function renderFilas(data) {
        tbody.innerHTML = "";

        if (!data || data.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="10" class="text-center text-muted">No se encontraron resultados</td>
                </tr>
            `;
            paginator.refresh();
            return;
        }

        data.forEach(inv => {
            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td style="text-align:center;">${inv.codigoBarras ?? ""}</td>
                <td style="text-align:center;">${inv.insumo?.nombre ?? ""}</td>
                <td style="text-align:center;">${inv.insumo?.tipo ?? ""}</td>
                <td style="text-align:center;">${inv.stockActual}</td>
                <td style="text-align:center;">${inv.stockBloqueado}</td>
                <td style="text-align:center;">${inv.stockMinimo}</td>
                <td style="text-align:center;">${inv.fechaVencimiento ?? ""}</td>
                <td style="text-align:center;">${inv.fechaApertura ?? ""}</td>
                <td style="text-align:center;">${inv.activo ? "Activo" : "Inactivo"}</td>
                <td style="text-align:center;">
                    <a href="/inventario/modificar/${inv.idInventario}" class="btn-edit">
                        <i class="bi bi-pencil"></i>
                    </a>
                </td>
            `;
            tbody.appendChild(fila);
        });

        paginator.refresh();
    }

    if (input) {
        input.addEventListener("input", function () {
            const query = input.value.trim();

            tbody.innerHTML = `
                <tr>
                    <td colspan="10" class="text-center text-muted">Buscando...</td>
                </tr>
            `;

            fetch(`/inventario/buscarJSON?query=${encodeURIComponent(query)}`)
                .then(response => {
                    if (!response.ok) throw new Error("Error al buscar inventario");
                    return response.json();
                })
                .then(data => {
                    renderFilas(data);
                })
                .catch(error => {
                    console.error("Error búsqueda inventario:", error);
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="10" class="text-center text-danger">Error al cargar resultados</td>
                        </tr>
                    `;
                    paginator.refresh();
                });
        });
    }
});