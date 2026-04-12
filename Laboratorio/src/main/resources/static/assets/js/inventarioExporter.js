function exportarPDFInventario() {
    const query = document.getElementById("busquedaInventarioInput").value;
    window.location.href = '/inventario/export/pdf?query=' + encodeURIComponent(query);
}

function exportarExcelInventario() {
    const query = document.getElementById("busquedaInventarioInput").value;
    window.location.href = '/inventario/export/excel?query=' + encodeURIComponent(query);
}
