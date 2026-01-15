document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("busquedaUsuarioInput");
  const tbody = document.getElementById("tbodyUsuarios");

  if (!input || !tbody) return;

  input.addEventListener("input", function () {
    const query = input.value.trim();

    tbody.innerHTML = `
      <tr>
        <td colspan="12" class="text-center text-muted">Buscando...</td>
      </tr>
    `;

    fetch(`/usuario/buscarJSON?query=${encodeURIComponent(query)}`)
      .then((response) => {
        if (!response.ok) throw new Error("Error al buscar usuarios");
        return response.json();
      })
      .then((data) => {
        tbody.innerHTML = "";

        if (data.length === 0) {
          tbody.innerHTML = `
            <tr>
              <td colspan="12" class="text-center text-muted">No se encontraron usuarios</td>
            </tr>
          `;
          return;
        }

        data.forEach((u) => {
          const fila = document.createElement("tr");
          fila.innerHTML = `
            <td style="text-align:center;">${u.nombre}</td>
            <td style="text-align:center;">${u.primerApellido}</td>
            <td style="text-align:center;">${u.segundoApellido}</td>
            <td style="text-align:center;">${u.username}</td>
            <td style="text-align:center;">${u.cedula}</td>
            <td style="text-align:center;">${u.activo ? "Activo" : "Inactivo"}</td>

            <td style="text-align:center;">
                <a href="/usuario/modificar/${u.idUsuario}" class="btn-edit" title="Editar">
                    <i class="bi bi-pencil"></i>
                </a>
            </td>
          `;
          tbody.appendChild(fila);
        });
      })
      .catch((error) => {
        console.error("Error búsqueda usuarios:", error);
        tbody.innerHTML = `
          <tr>
            <td colspan="12" class="text-center text-danger">Error al cargar resultados</td>
          </tr>
        `;
      });
  });
});

document.addEventListener("DOMContentLoaded", function () {

    const FILAS_POR_PAGINA = 25;

    const tbody = document.getElementById("tbodyUsuarios");
    if (!tbody) return;

    let filas = Array.from(tbody.querySelectorAll("tr"));

    if (filas.length <= FILAS_POR_PAGINA) return;

    const pagWrapper = document.createElement("div");
    pagWrapper.className = "d-flex justify-content-center mt-4";

    const nav = document.createElement("nav");
    const ul = document.createElement("ul");
    ul.className = "pagination pagination-sm";

    nav.appendChild(ul);
    pagWrapper.appendChild(nav);

    tbody.closest(".card-body").appendChild(pagWrapper);

    let paginaActual = 1;

    function mostrarPagina(pagina) {
        paginaActual = pagina;

        const inicio = (pagina - 1) * FILAS_POR_PAGINA;
        const fin = inicio + FILAS_POR_PAGINA;

        filas.forEach((fila, index) => {
            fila.style.display =
                index >= inicio && index < fin ? "" : "none";
        });

        renderizarPaginacion();
    }

    function renderizarPaginacion() {
        ul.innerHTML = "";

        const totalPaginas = Math.ceil(filas.length / FILAS_POR_PAGINA);

        ul.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

        for (let i = 1; i <= totalPaginas; i++) {
            const li = crearBoton(i, i, false);
            if (i === paginaActual) li.classList.add("active");
            ul.appendChild(li);
        }

        ul.appendChild(
            crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas)
        );
    }

    function crearBoton(texto, pagina, deshabilitado) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (deshabilitado) li.classList.add("disabled");

        const a = document.createElement("a");
        a.href = "#";
        a.className = "page-link";
        a.textContent = texto;

        a.style.color = "var(--text-color, #333)";
        a.style.backgroundColor = "transparent";
        a.style.borderColor = "var(--border-color, #ccc)";

        a.addEventListener("click", e => {
            e.preventDefault();
            if (!deshabilitado) mostrarPagina(pagina);
        });

        li.appendChild(a);
        return li;
    }

    mostrarPagina(1);
});

