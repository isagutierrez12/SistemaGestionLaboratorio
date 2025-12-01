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
