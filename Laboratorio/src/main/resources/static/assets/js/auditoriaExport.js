function exportarPDF() {
    const usuario = document.getElementById("filtroUsuario").value;
    const modulo = document.getElementById("filtroModulo").value;
    const accion = document.getElementById("filtroAccion").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;

    const params = new URLSearchParams({
        usuario,
        modulo,
        accion,
        fechaInicio,
        fechaFin
    });

    window.location.href = "/auditoria/export/pdf?" + params.toString();
}

function exportarExcel() {
    const usuario = document.getElementById("filtroUsuario").value;
    const modulo = document.getElementById("filtroModulo").value;
    const accion = document.getElementById("filtroAccion").value;
    const fechaInicio = document.getElementById("fechaInicio").value;
    const fechaFin = document.getElementById("fechaFin").value;

    const params = new URLSearchParams({
        usuario,
        modulo,
        accion,
        fechaInicio,
        fechaFin
    });

    window.location.href = "/auditoria/export/excel?" + params.toString();
}
