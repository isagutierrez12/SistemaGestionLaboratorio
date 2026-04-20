document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("busquedaInput");
    const catalog = document.querySelector(".catalog");

    if (!catalog) return;

    const paginator = setupTablePaginator({
        container: catalog,
        itemSelector: ".test",
        pageSize: 6
    });

    function renderItems(data) {
        catalog.innerHTML = "";

        if (!data || data.length === 0) {
            catalog.innerHTML = `
                <div class="text-center text-muted mt-3">No se encontraron exámenes</div>
            `;
            paginator.refresh();
            return;
        }

        data.forEach(e => {
            const div = document.createElement("div");
            div.classList.add("test");
            div.innerHTML = `
                <div class="test-header">
                    <span class="code">${e.codigo}</span>
                    <h3>${e.nombre}</h3>
                </div>

                <div class="test-body">
                    <div class="test-info">
                        <div class="info-row"><strong>Area:</strong> ${e.area}</div>
                        <div class="info-row"><strong>Condiciones:</strong> ${e.condiciones ?? ""}</div>
                        <div class="info-row"><strong>Precio:</strong> $${e.precio}</div>
                        <div class="info-row">
                            <strong>Rango:</strong> ${e.valorMinimo} - ${e.valorMaximo} ${e.unidad}
                        </div>
                        <div class="info-row description">
                            <strong>Estado:</strong> ${e.activo ? "Activo" : "Inactivo"}
                        </div>
                    </div>
                </div>

                <div class="test-footer">
                    <a href="/examen/modificar/${e.idExamen}" class="btn-exam" title="Edit">
                        <i class="bi bi-pencil"></i>
                    </a>
                    <a href="/examen/${e.idExamen}/insumos" class="btn-exam" title="Insumos">
                        <i class="bi bi-box-seam"></i>
                    </a>
                </div>
            `;
            catalog.appendChild(div);
        });

        paginator.refresh();
    }

    if (input) {
        input.addEventListener("input", function () {
            const query = input.value.trim();

            catalog.innerHTML = `
                <div class="text-center text-muted mt-3">Buscando...</div>
            `;

            fetch(`/examen/buscar/json?query=${encodeURIComponent(query)}`)
                .then(response => {
                    if (!response.ok) throw new Error("Error al buscar exámenes");
                    return response.json();
                })
                .then(data => {
                    renderItems(data);
                })
                .catch(err => {
                    console.error("Error en búsqueda dinámica:", err);
                    catalog.innerHTML = `
                        <div class="text-center text-danger mt-3">Error al cargar resultados</div>
                    `;
                    paginator.refresh();
                });
        });
    }
});