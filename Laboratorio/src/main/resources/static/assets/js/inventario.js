document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("busquedaInventarioInput");
  const tbody = document.getElementById("tbodyInventario");

  if (!input || !tbody) return;

  input.addEventListener("input", function () {
    const query = input.value.trim();

    tbody.innerHTML = `
      <tr>
        <td colspan="10" class="text-center text-muted">Buscando...</td>
      </tr>
    `;

    fetch(`/inventario/buscarJSON?query=${encodeURIComponent(query)}`)
      .then((response) => {
        if (!response.ok) throw new Error("Error al buscar inventario");
        return response.json();
      })
      .then((data) => {
        tbody.innerHTML = "";

        if (data.length === 0) {
          tbody.innerHTML = `
            <tr>
              <td colspan="10" class="text-center text-muted">No se encontraron resultados</td>
            </tr>
          `;
          return;
        }

        data.forEach((inv) => {
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
      })
      .catch((error) => {
        console.error("Error búsqueda inventario:", error);
        tbody.innerHTML = `
          <tr>
            <td colspan="10" class="text-center text-danger">Error al cargar resultados</td>
          </tr>
        `;
      });
  });
});

document.addEventListener("DOMContentLoaded", function () {

    const ITEMS_POR_PAGINA = 25;
    const MAX_PAGINAS_VISIBLES = 10;

    const tbody = document.getElementById("tbodyInventario");

    if (!tbody) return;

    const filas = Array.from(tbody.querySelectorAll("tr"));
    const totalItems = filas.length;

    if (totalItems <= ITEMS_POR_PAGINA) return;

    let paginaActual = 1;

    const paginacionWrapper = document.createElement("div");
    paginacionWrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    paginacionWrapper.appendChild(nav);
    tbody.closest(".card-body").appendChild(paginacionWrapper);

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        filas.forEach(f => f.style.display = "none");

        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        filas.slice(inicio, fin).forEach(f => f.style.display = "");

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(totalItems / ITEMS_POR_PAGINA);

        ul.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

        let inicio = Math.max(1, paginaActual - Math.floor(MAX_PAGINAS_VISIBLES / 2));
        let fin = inicio + MAX_PAGINAS_VISIBLES - 1;

        if (fin > totalPaginas) {
            fin = totalPaginas;
            inicio = Math.max(1, fin - MAX_PAGINAS_VISIBLES + 1);
        }

        if (inicio > 1) {
            ul.appendChild(crearBoton(1, 1));
            ul.appendChild(crearEllipsis());
        }

        for (let i = inicio; i <= fin; i++) {
            const li = crearBoton(i, i);
            if (i === paginaActual) li.classList.add("active");
            ul.appendChild(li);
        }

        if (fin < totalPaginas) {
            ul.appendChild(crearEllipsis());
            ul.appendChild(crearBoton(totalPaginas, totalPaginas));
        }

        ul.appendChild(crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas));
    }

    function crearBoton(texto, pagina, deshabilitado = false) {
        const li = document.createElement("li");
        li.className = "page-item pagination-neutral";
        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.className = "page-link";
        a.href = "#";
        a.textContent = texto;

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    function crearEllipsis() {
        const li = document.createElement("li");
        li.className = "page-item disabled";

        const span = document.createElement("span");
        span.className = "page-link";
        span.textContent = "...";

        li.appendChild(span);
        return li;
    }

    mostrarPagina(1);
});

