(function (global) {
    function setupTablePaginator(opts) {
        // Modo tabla (tbody -> tr) o modo container genérico
        const container = opts.tbody || opts.container;
        const itemSelector = opts.tbody ? "tr" : (opts.itemSelector || "*");
        const pageSize = opts.pageSize || 10;
        const maxButtons = opts.maxButtons || 10;

        if (!container) return null;

        // Busca dónde colocar los controles
        const hostEl = opts.controlsHost
                || container.closest(".card-body")
                || container.closest("form")
                || container.parentElement;

        // Wrapper reutilizable
        let wrapper = hostEl.querySelector(":scope > .js-paginator-wrapper");
        if (!wrapper) {
            wrapper = document.createElement("div");
            wrapper.className = "js-paginator-wrapper d-flex justify-content-center mt-4";
            const nav = document.createElement("nav");
            const ul = document.createElement("ul");
            ul.className = "pagination pagination-sm";
            nav.appendChild(ul);
            wrapper.appendChild(nav);
            hostEl.appendChild(wrapper);
        }
        const ul = wrapper.querySelector("ul");

        let paginaActual = 1;

        function itemsValidos() {
            // Solo hijos directos que matcheen el selector
            const all = Array.from(container.children).filter(el => el.matches(itemSelector));
            // Para modo tabla, filtrar placeholders (<tr> con un solo <td colspan>)
            if (opts.tbody) {
                return all.filter(tr => {
                    const cells = tr.querySelectorAll("td");
                    if (cells.length === 0) return false;
                    if (cells.length === 1 && cells[0].hasAttribute("colspan")) return false;
                    return true;
                });
            }
            return all;
        }

        function mostrarPagina(pagina) {
            const items = itemsValidos();
            const totalPaginas = Math.max(1, Math.ceil(items.length / pageSize));

            if (pagina < 1) pagina = 1;
            if (pagina > totalPaginas) pagina = totalPaginas;
            paginaActual = pagina;

            const inicio = (pagina - 1) * pageSize;
            const fin = inicio + pageSize;

            items.forEach((it, i) => {
                it.style.display = (i >= inicio && i < fin) ? "" : "none";
            });

            renderizarControles(totalPaginas);
        }

        function renderizarControles(totalPaginas) {
            ul.innerHTML = "";

            const items = itemsValidos();
            if (items.length <= pageSize) {
                wrapper.style.display = "none";
                return;
            }
            wrapper.style.display = "";

            ul.appendChild(crearBoton("Anterior", paginaActual - 1, paginaActual === 1));

            let inicio = Math.max(1, paginaActual - Math.floor(maxButtons / 2));
            let fin = inicio + maxButtons - 1;
            if (fin > totalPaginas) {
                fin = totalPaginas;
                inicio = Math.max(1, fin - maxButtons + 1);
            }

            if (inicio > 1) {
                ul.appendChild(crearBoton(1, 1, false));
                if (inicio > 2) ul.appendChild(crearEllipsis());
            }

            for (let i = inicio; i <= fin; i++) {
                const li = crearBoton(i, i, false);
                if (i === paginaActual) li.classList.add("active");
                ul.appendChild(li);
            }

            if (fin < totalPaginas) {
                if (fin < totalPaginas - 1) ul.appendChild(crearEllipsis());
                ul.appendChild(crearBoton(totalPaginas, totalPaginas, false));
            }

            ul.appendChild(crearBoton("Siguiente", paginaActual + 1, paginaActual === totalPaginas));
        }

        function crearBoton(texto, pagina, deshabilitado) {
            const li = document.createElement("li");
            li.className = "page-item";
            if (deshabilitado) li.classList.add("disabled");

            const a = document.createElement("a");
            a.className = "page-link";
            a.href = "#";
            a.textContent = texto;
            a.style.color = "#000";
            a.style.backgroundColor = "#fff";
            a.style.borderColor = "#ccc";

            a.addEventListener("click", e => {
                e.preventDefault();
                if (!deshabilitado) mostrarPagina(pagina);
            });

            li.appendChild(a);
            return li;
        }

        function crearEllipsis() {
            const li = document.createElement("li");
            li.className = "page-item disabled";
            const span = document.createElement("span");
            span.className = "page-link";
            span.textContent = "...";
            li.appendChild(span);
            return li;
        }

        mostrarPagina(1);

        return {
            refresh() { mostrarPagina(1); },
            goTo(p)    { mostrarPagina(p); },
            currentPage() { return paginaActual; }
        };
    }

    global.setupTablePaginator = setupTablePaginator;
})(window);