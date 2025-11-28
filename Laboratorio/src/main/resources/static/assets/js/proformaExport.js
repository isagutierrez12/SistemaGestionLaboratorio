function exportarProformaPDF() {

    const element = document.querySelector(".proforma-container");
    if (!element) {
        console.error("No se encontró '.proforma-container'");
        return;
    }

    const opt = {
        margin: 0.5,
        filename: 'Proforma.pdf',
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: {
            scale: 2, 
            useCORS: true
        },
        jsPDF: {
            unit: 'in',
            format: 'letter',
            orientation: 'portrait'
        }
    };

    html2pdf().set(opt).from(element).save();
}

function imprimirProforma() {
    const element = document.querySelector(".proforma-container");
    if (!element) {
        console.error("No se encontró '.proforma-container'");
        return;
    }

    const ventana = window.open('', '_blank', 'width=800,height=600');

    const estilos = Array.from(document.querySelectorAll("link[rel='stylesheet'], style"))
                         .map(node => node.outerHTML)
                         .join('\n');

    ventana.document.write(`
        <html>
            <head>
                <title>Imprimir Proforma</title>
                ${estilos}
            </head>
            <body>
                ${element.outerHTML}
            </body>
        </html>
    `);

    ventana.document.close();
    ventana.focus();

    setTimeout(() => {
        ventana.print();
    }, 500);
}

