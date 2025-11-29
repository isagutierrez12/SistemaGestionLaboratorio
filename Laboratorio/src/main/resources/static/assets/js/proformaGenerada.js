document.addEventListener("DOMContentLoaded", function () {

    const btnContinuar = document.getElementById("btnContinuar");
    const checks = document.querySelectorAll(".exam-check");

    const btnSeleccionarTodo = document.getElementById("btnSeleccionarTodo");
    const btnDeseleccionarTodo = document.getElementById("btnDeseleccionarTodo");

    function actualizarBoton() {
        const algunoSeleccionado = Array.from(checks).some(ch => ch.checked);
        btnContinuar.disabled = !algunoSeleccionado;
    }

    checks.forEach(ch => {
        ch.addEventListener("change", actualizarBoton);
    });

    btnSeleccionarTodo.addEventListener("click", () => {
        checks.forEach(ch => {
            ch.checked = true;
            ch.style.backgroundColor = "#1c94a4";
            ch.style.borderColor = "#1c94a4";
        });
        actualizarBoton();
    });

    btnDeseleccionarTodo.addEventListener("click", () => {
        checks.forEach(ch => {
            ch.checked = false;
            ch.style.backgroundColor = "transparent";
            ch.style.borderColor = "#1c94a4";
        });
        actualizarBoton();
    });

});
