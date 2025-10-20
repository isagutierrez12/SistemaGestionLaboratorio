document.addEventListener("DOMContentLoaded", function () {
    const inputBuscar = document.getElementById("buscarUsuarioInput");
    const btnBuscar = document.getElementById("btnBuscarUsuario");
    const tbody = document.getElementById("tbodyUsuarios");

    function actualizarTabla(usuarios) {
        tbody.innerHTML = "";
        if (usuarios.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">No se encontraron usuarios</td></tr>`;
            return;
        }

        usuarios.forEach(u => {
            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td style="text-align:center;">${u.nombre}</td>
                <td style="text-align:center;">${u.primerApellido}</td>
                <td style="text-align:center;">${u.segundoApellido}</td>
                <td style="text-align:center;">${u.username}</td>
                <td style="text-align:center;">${u.cedula}</td>
                <td style="text-align:center;">${u.activo ? 'Activo' : 'Inactivo'}</td>
                <td style="text-align:center;">
                    <a href="/usuario/modificar/${u.idUsuario}" class="btn-edit" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </a>
                </td>
            `;
            tbody.appendChild(fila);
        });
    }

    function buscarUsuarios() {
        const nombre = inputBuscar.value.trim();
        fetch(`/usuario/buscarJSON?nombre=${encodeURIComponent(nombre)}`)
            .then(response => response.json())
            .then(data => actualizarTabla(data))
            .catch(err => console.error(err));
    }

    btnBuscar.addEventListener("click", buscarUsuarios);
    inputBuscar.addEventListener("input", buscarUsuarios); // búsqueda dinámica

    buscarUsuarios(); // inicializar con todos los usuarios
});
