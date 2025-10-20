document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("busquedaInput");
  const tbody = document.getElementById("tbodyPacientes");

  if (!input || !tbody) return; // seguridad si el fragmento no está cargado

  input.addEventListener("input", function () {
    const query = input.value.trim();

    // Muestra mientras carga
    tbody.innerHTML = `
      <tr>
        <td colspan="12" class="text-center text-muted">Buscando...</td>
      </tr>
    `;

    fetch(`/paciente/buscar/json?query=${encodeURIComponent(query)}`)
      .then((response) => {
        if (!response.ok) throw new Error("Error al buscar pacientes");
        return response.json();
      })
      .then((data) => {
        tbody.innerHTML = ""; // Limpia el contenido anterior

        if (data.length === 0) {
          tbody.innerHTML = `
            <tr>
              <td colspan="12" class="text-center text-muted">No se encontraron pacientes</td>
            </tr>
          `;
          return;
        }

        data.forEach((p) => {
          const fila = document.createElement("tr");
          fila.innerHTML = `
            <td style="text-align:center;">${p.nombre}</td>
            <td style="text-align:center;">${p.primerApellido}</td>
            <td style="text-align:center;">${p.segundoApellido}</td>
            <td style="text-align:center;">${p.cedula}</td>
            <td style="text-align:center;">${p.fechaNacimiento || ""}</td>
            <td style="text-align:center;">${p.telefono || ""}</td>
            <td style="text-align:center;">${p.email || ""}</td>
            <td style="text-align:center;">${p.contactoEmergencia || ""}</td>
            <td style="text-align:center;">${p.padecimiento || ""}</td>
            <td style="text-align:center;">${p.alergia || ""}</td>
            <td style="text-align:center;">${p.activo ? "Activo" : "Inactivo"}</td>
            <td style="text-align:center;">
                <a href="/paciente/editar/${p.idPaciente}" class="btn-edit" title="Editar">
                    <i class="bi bi-pencil"></i>
                </a>
                <a href="/paciente/historial/${p.idPaciente}" class="btn btn-history" title="Historial">
                    Historial
                </a>
            </td>
          `;
          tbody.appendChild(fila);
        });
      })
      .catch((error) => {
        console.error("Error en búsqueda dinámica:", error);
        tbody.innerHTML = `
          <tr>
            <td colspan="12" class="text-center text-danger">Error al cargar los resultados</td>
          </tr>
        `;
      });
  });
});
