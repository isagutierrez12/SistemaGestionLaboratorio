function exportarPDF() {
    const usuario = document.getElementById("filtroUsuarioCrit").value;
    const tipoEvento = document.getElementById("filtroTipoEventoCrit").value;
    const fechaInicio = document.getElementById("fechaInicioCrit").value;
    const fechaFin = document.getElementById("fechaFinCrit").value;

    const params = new URLSearchParams({ usuario, tipoEvento, fechaInicio, fechaFin });
    window.location.href = "/auditoria/criticos/export/pdf?" + params.toString();
}

function exportarExcel() {
    const usuario = document.getElementById("filtroUsuarioCrit").value;
    const tipoEvento = document.getElementById("filtroTipoEventoCrit").value;
    const fechaInicio = document.getElementById("fechaInicioCrit").value;
    const fechaFin = document.getElementById("fechaFinCrit").value;

    const params = new URLSearchParams({ usuario, tipoEvento, fechaInicio, fechaFin });
    window.location.href = "/auditoria/criticos/export/excel?" + params.toString();
}
