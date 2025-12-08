document.addEventListener("DOMContentLoaded", () => {

    const API_BASE = "/api/dashboard";
    const kpiIngresos = document.getElementById("kpiIngresos");
    const kpiPacientes = document.getElementById("kpiPacientes");
    const kpiPromedio = document.getElementById("kpiPromedioExamenes");
    const lblIng = document.getElementById("kpiPeriodoIngresos");
    const lblPac = document.getElementById("kpiPeriodoPacientes");
    const lblProm = document.getElementById("kpiPeriodoPromedio");
    const filtroDesde = document.getElementById("filtroDesde");
    const filtroHasta = document.getElementById("filtroHasta");
    const filtroPeriodo = document.getElementById("filtroPeriodo");

    const filtroArea = document.getElementById("filtroArea");
    const btnExportarTopPdf = document.getElementById("btnExportarTopPdf");
    const btnExportarTopExcel = document.getElementById("btnExportarTopExcel");
    const formTopPdf = document.getElementById("formTopPdf");
    const inputChartImage = document.getElementById("chartImageInput");

    const btnAplicar = document.getElementById("btnAplicarFiltros");
    const filtroEstadoReporte = document.getElementById("filtroEstadoReporte");
    const filtroNombreExamenReporte = document.getElementById("filtroNombreExamenReporte");
    const filtroAreaReporte = document.getElementById("filtroAreaReporte");
    const tablaReporteBody = document.querySelector("#tablaReporteExamenes tbody");
    const resumenReporte = document.getElementById("resumenReporte");
    const btnGenerarReporte = document.getElementById("btnGenerarReporte");
    const btnExportarReportePdf = document.getElementById("btnExportarReportePdf");
    const btnExportarReporteExcel = document.getElementById("btnExportarReporteExcel");

    const filtroTipoAlerta = document.getElementById("filtroTipoAlerta");
    const tablaAlertasBody = document.querySelector("#tablaAlertasInventario tbody");

    const hoy = new Date();
    const yyyy = hoy.getFullYear();
    const mm = String(hoy.getMonth() + 1).padStart(2, '0');
    const dd = String(hoy.getDate()).padStart(2, '0');
    const hoyStr = `${yyyy}-${mm}-${dd}`;
    filtroDesde.value = hoyStr;
    filtroHasta.value = hoyStr;
    filtroPeriodo.value = "DIA";
    let topExamsChart = null;
    function formatoCRC(monto) {
        return monto.toLocaleString('es-CR', {
            style: 'currency',
            currency: 'CRC',
            minimumFractionDigits: 2
        });
    }

    function getPeriodoLabel() {
        switch (filtroPeriodo.value) {
            case "DIA":
                return "| Hoy";
            case "SEMANA":
                return "| Esta semana";
            case "MES":
                return "| Este mes";
            case "TRIMESTRE":
                return "| Este trimestre";
            case "ANIO":
                return "| Este año";
            default:
                return "| Personalizado";
        }
    }

    function capitalizar(texto) {
        if (!texto)
            return "";
        return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
    }

    function actualizarRangoFechasPorPeriodo() {

        const hoy = new Date();
        const yyyy = hoy.getFullYear();
        const mm = String(hoy.getMonth() + 1).padStart(2, '0');
        const dd = String(hoy.getDate()).padStart(2, '0');
        const hoyStr = `${yyyy}-${mm}-${dd}`;

        if (filtroPeriodo.value === "DIA") {
            filtroDesde.value = hoyStr;
            filtroHasta.value = hoyStr;
            return;
        }

        if (filtroPeriodo.value === "SEMANA") {
            const date = new Date();
            const day = date.getDay();
            const diff = date.getDate() - day + (day === 0 ? -6 : 1); // lunes
            const lunes = new Date(date.setDate(diff));

            const yyyyL = lunes.getFullYear();
            const mmL = String(lunes.getMonth() + 1).padStart(2, '0');
            const ddL = String(lunes.getDate()).padStart(2, '0');

            filtroDesde.value = `${yyyyL}-${mmL}-${ddL}`;
            filtroHasta.value = hoyStr;
            return;
        }

        if (filtroPeriodo.value === "MES") {
            const primeroMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
            const yyyyP = primeroMes.getFullYear();
            const mmP = String(primeroMes.getMonth() + 1).padStart(2, '0');
            const ddP = String(primeroMes.getDate()).padStart(2, '0');

            filtroDesde.value = `${yyyyP}-${mmP}-${ddP}`;
            filtroHasta.value = hoyStr;
            return;
        }

        if (filtroPeriodo.value === "TRIMESTRE") {
            const currentQuarter = Math.floor(hoy.getMonth() / 3); // 0,1,2,3
            const startMonth = currentQuarter * 3; // 0,3,6,9

            const inicioTrimestre = new Date(hoy.getFullYear(), startMonth, 1);

            const yyyyT = inicioTrimestre.getFullYear();
            const mmT = String(inicioTrimestre.getMonth() + 1).padStart(2, '0');
            const ddT = "01";

            filtroDesde.value = `${yyyyT}-${mmT}-${ddT}`;
            filtroHasta.value = hoyStr;
            return;
        }

        if (filtroPeriodo.value === "ANIO") {
            filtroDesde.value = `${yyyy}-01-01`;
            filtroHasta.value = hoyStr;
            return;
        }
    }

    function buildTopExamsQueryParams() {
        const params = new URLSearchParams();
        if (filtroDesde.value)
            params.append("desde", filtroDesde.value);
        if (filtroHasta.value)
            params.append("hasta", filtroHasta.value);
        if (filtroArea.value)
            params.append("area", filtroArea.value);
        params.append("limite", "10"); // mismo límite que el gráfico
        return params;
    }


    function buildReporteQueryParams() {
        const params = new URLSearchParams();
        if (filtroDesde.value)
            params.append("desde", filtroDesde.value);
        if (filtroHasta.value)
            params.append("hasta", filtroHasta.value);
        if (filtroAreaReporte && filtroAreaReporte.value)
            params.append("area", filtroAreaReporte.value);
        if (filtroEstadoReporte && filtroEstadoReporte.value)
            params.append("estado", filtroEstadoReporte.value);
        if (filtroNombreExamenReporte && filtroNombreExamenReporte.value)
            params.append("examen", filtroNombreExamenReporte.value);

        return params;
    }

    function clasePorAlerta(dto) {
        const belowMin = dto.stockActual < dto.stockMinimo;
        if (dto.bajoStock && dto.proximoVencer) {
            return "table-danger";
        }
        if (belowMin) {
            return "table-danger";
        }
        if (dto.bajoStock || dto.proximoVencer) {
            return "table-warning";
        }
        return "";
    }

    function marcarPeriodoPersonalizado() {
        if (filtroPeriodo) {
            filtroPeriodo.value = "";
        }
    }

    if (filtroDesde) {
        filtroDesde.addEventListener("change", marcarPeriodoPersonalizado);
    }
    if (filtroHasta) {
        filtroHasta.addEventListener("change", marcarPeriodoPersonalizado);
    }

    async function cargarAreas() {
        try {
            const resp = await fetch(`${API_BASE}/areas`);
            if (!resp.ok) {
                console.error("No se pudieron cargar las áreas");
                return;
            }

            const areas = await resp.json();

            //Dropdown del dashboard
            filtroArea.innerHTML = "";
            const optTodas = document.createElement("option");
            optTodas.value = "";
            optTodas.textContent = "Todas las áreas";
            filtroArea.appendChild(optTodas);

            areas.forEach(a => {
                const opt = document.createElement("option");
                opt.value = a;
                opt.textContent = capitalizar(a);
                filtroArea.appendChild(opt);
            });

            //Dropdown del reporte
            if (typeof filtroAreaReporte !== "undefined" && filtroAreaReporte) {
                filtroAreaReporte.innerHTML = "";

                const optAll = document.createElement("option");
                optAll.value = "";
                optAll.textContent = "Todas las áreas";
                filtroAreaReporte.appendChild(optAll);

                areas.forEach(a => {
                    const opt = document.createElement("option");
                    opt.value = a;
                    opt.textContent = capitalizar(a);
                    filtroAreaReporte.appendChild(opt);
                });
            }

        } catch (e) {
            console.error("Error cargando áreas:", e);
        }
    }


    async function cargarResumen() {
        const params = new URLSearchParams();
        if (filtroPeriodo.value) {
            params.append("periodo", filtroPeriodo.value);
        } else {
            if (filtroDesde.value)
                params.append("desde", filtroDesde.value);
            if (filtroHasta.value)
                params.append("hasta", filtroHasta.value);
        }

        const resp = await fetch(`${API_BASE}/resumen?` + params.toString());
        if (!resp.ok) {
            console.error("Error cargando resumen");
            return;
        }
        const data = await resp.json();
        const ingresos = Number(data.ingresosTotales ?? 0);
        const pacientes = Number(data.pacientesAtendidos ?? 0);
        const promedio = Number(data.promedioExamenesPorPaciente ?? 0);
        kpiIngresos.textContent = formatoCRC(ingresos);
        kpiPacientes.textContent = pacientes.toString();
        kpiPromedio.textContent = promedio.toFixed(2);
        const label = getPeriodoLabel();
        lblIng.textContent = " " + label;
        lblPac.textContent = " " + label;
        lblProm.textContent = " " + label;
    }

    async function cargarTopExamenes() {
        const params = buildTopExamsQueryParams();

        const resp = await fetch(`${API_BASE}/top-examenes?` + params.toString());
        if (!resp.ok) {
            console.error("Error cargando top exámenes");
            return;
        }
        const data = await resp.json();
        const nombres = data.map(d => d.nombreExamen);
        const cantidades = data.map(d => d.cantidad);
        if (!topExamsChart) {
            topExamsChart = new ApexCharts(document.querySelector("#topExamsChart"), {
                series: [{
                        name: "Cantidad de solicitudes",
                        data: cantidades
                    }],
                colors: ["#51B6C4"],
                chart: {
                    type: 'bar',
                    height: 350,
                    toolbar: {show: false}
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: '60%',
                        borderRadius: 4
                    }
                },
                dataLabels: {enabled: false},
                xaxis: {
                    categories: nombres,
                    labels: {rotate: -45}
                },
                yaxis: {
                    title: {text: 'Número de solicitudes'}
                }
            });
            topExamsChart.render();
        } else {
            topExamsChart.updateOptions({
                xaxis: {categories: nombres},
                series: [{name: "Cantidad de solicitudes", data: cantidades}]
            });
        }
    }

    async function cargarReporteExamenes() {
        try {
            const params = buildReporteQueryParams();
            const resp = await fetch(`${API_BASE}/reportes-examenes?` + params.toString());
            if (!resp.ok) {
                console.error("Error al obtener reporte de exámenes");
                return;
            }

            const data = await resp.json();
            tablaReporteBody.innerHTML = "";
            resumenReporte.textContent = "";
            if (!data || data.length === 0) {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td colspan="6" class="text-center text-muted">
                        No se encontraron resultados para los filtros aplicados.
                    </td>
                `;
                tablaReporteBody.appendChild(tr);
                resumenReporte.textContent = "Sin datos para mostrar.";
                return;
            }

            let totalMonto = 0;
            const porArea = {};
            data.forEach(item => {
                const montoNum = item.monto != null ? Number(item.monto) : null;
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${item.fecha || ""}</td>
                    <td>${item.paciente || ""}</td>
                    <td>${item.examen || ""}</td>
                    <td>${item.area || ""}</td>
                    <td>${item.estado || ""}</td>
                    <td>${montoNum != null ? montoNum.toFixed(2) : ""}</td>
                `;
                tablaReporteBody.appendChild(tr);
                if (montoNum != null && !Number.isNaN(montoNum)) {
                    totalMonto += montoNum;
                }
                const area = item.area || "Sin área";
                porArea[area] = (porArea[area] || 0) + 1;
            });
            const totalReg = data.length;
            const partesArea = Object.entries(porArea)
                    .map(([area, cant]) => `${area}: ${cant}`)
                    .join(" | ");
            resumenReporte.textContent =
                    `Total de registros: ${totalReg} | Monto total: ₡${totalMonto.toFixed(2)} ` +
                    (partesArea ? `| Registros por área: ${partesArea}` : "");
        } catch (e) {
            console.error("Error en cargarReporteExamenes", e);
        }
    }

    async function cargarAlertasInventario() {
        try {
            const tipo = filtroTipoAlerta ? filtroTipoAlerta.value : "TODAS";
            const params = new URLSearchParams();
            params.append("tipo", tipo || "TODAS");
            const resp = await fetch(`${API_BASE}/inventario-alertas?` + params.toString());
            if (!resp.ok) {
                console.error("Error cargando alertas de inventario");
                return;
            }

            const data = await resp.json();
            tablaAlertasBody.innerHTML = "";
            if (!data || data.length === 0) {
                const trVacio = document.createElement("tr");
                trVacio.innerHTML =
                        `<td colspan="8" class="text-center text-muted">
                        No hay insumos en alerta.
                    </td>`;
                tablaAlertasBody.appendChild(trVacio);
                return;
            }

            data.forEach(dto => {
                const tr = document.createElement("tr");
                const clase = clasePorAlerta(dto);
                if (clase) {
                    tr.classList.add(clase);
                }

                tr.innerHTML = `
                    <td>${dto.codigoBarras || ""}</td>
                    <td>${dto.nombreInsumo}</td>
                    <td>${dto.tipoInsumo || ""}</td>
                    <td>${dto.stockActual}</td>
                    <td>${dto.stockMinimo}</td>
                    <td>${dto.fechaVencimiento || "-"}</td>
                    <td>${dto.diasParaVencer != null ? dto.diasParaVencer : "-"}</td>
                    <td>${dto.etiquetaAlerta}</td>
                `;
                tablaAlertasBody.appendChild(tr);
            });
        } catch (e) {
            console.error("Error cargando alertas de inventario:", e);
        }
    }

    async function refrescarDashboard() {
        await Promise.all([
            cargarResumen(),
            cargarTopExamenes(),
            cargarAlertasInventario(),
            cargarReporteExamenes()
        ]);
    }

    btnAplicar.addEventListener("click", () => {
        actualizarRangoFechasPorPeriodo();
        refrescarDashboard();
    });

    filtroArea.addEventListener("change", refrescarDashboard);

    if (btnExportarTopPdf && formTopPdf && inputChartImage) {
        btnExportarTopPdf.addEventListener("click", async () => {
            if (!topExamsChart) {
                console.warn("El gráfico TopExams aún no está inicializado.");
                return;
            }

            try {
                const result = await topExamsChart.dataURI();
                const imgURI = result.imgURI;

                inputChartImage.value = imgURI;
                formTopPdf.submit();
            } catch (e) {
                console.error("No se pudo obtener la imagen del gráfico:", e);
            }
        });
    }


    if (btnExportarTopExcel) {
        btnExportarTopExcel.addEventListener("click", () => {
            const params = buildTopExamsQueryParams();
            window.location.href = `/dashboard/top-examenes/excel?` + params.toString();
        });
    }

    if (filtroAreaReporte) {
        filtroAreaReporte.addEventListener("change", () => {
            cargarReporteExamenes();
        });
    }

    if (btnGenerarReporte) {
        btnGenerarReporte.addEventListener("click", () => {
            if (filtroPeriodo.value) {
                actualizarRangoFechasPorPeriodo();
            }
            cargarReporteExamenes();
        });
    }

    if (btnExportarReportePdf) {
        btnExportarReportePdf.addEventListener("click", () => {
            if (filtroPeriodo.value) {
                actualizarRangoFechasPorPeriodo();
            }
            const params = buildReporteQueryParams();
            window.location.href = `/dashboard/reportes/examenes/pdf?` + params.toString();
        });
    }

    if (btnExportarReporteExcel) {
        btnExportarReporteExcel.addEventListener("click", () => {
            if (filtroPeriodo.value) {
                actualizarRangoFechasPorPeriodo();
            }
            const params = buildReporteQueryParams();
            window.location.href = `/dashboard/reportes/examenes/excel?` + params.toString();
        });
    }

    if (filtroTipoAlerta) {
        filtroTipoAlerta.addEventListener("change", () => {
            cargarAlertasInventario();
        });
    }

    async function init() {
        await cargarAreas();
        await refrescarDashboard();
    }

    init();
});