export function filterOperations() {
    const searchTerm = document.getElementById('operations-search').value.toLowerCase();
    const operationCards = document.querySelectorAll('.operation-card');

    operationCards.forEach(card => {
        const name = card.dataset.operationName.toLowerCase();
        const number = card.dataset.operationNumber?.toLowerCase() || '';
        const nomenclature = card.dataset.operationNomenclature.toLowerCase();
        const time = card.dataset.operationTime;

        const matches = name.includes(searchTerm) ||
            number.includes(searchTerm) ||
            nomenclature.includes(searchTerm) ||
            time.includes(searchTerm);

        card.style.display = matches ? 'block' : 'none';
    });
}

export function setupSearch() {
    const searchInput = document.getElementById('operations-search');
    const clearSearchBtn = document.getElementById('clear-search');
    const operationCards = document.querySelectorAll('.operation-card');

    function filterOperations() {
        const searchTerm = searchInput.value.toLowerCase();

        operationCards.forEach(card => {
            const name = card.getAttribute('data-operation-name').toLowerCase();
            const number = card.getAttribute('data-operation-number')?.toLowerCase() || '';
            const nomenclature = card.getAttribute('data-operation-nomenclature').toLowerCase();
            const time = card.getAttribute('data-operation-time');

            const matches = name.includes(searchTerm) ||
                number.includes(searchTerm) ||
                nomenclature.includes(searchTerm) ||
                time.includes(searchTerm);

            card.style.display = matches ? 'block' : 'none';
        });
    }

    searchInput.addEventListener('input', filterOperations);
    clearSearchBtn.addEventListener('click', function () {
        searchInput.value = '';
        filterOperations();
    });
}