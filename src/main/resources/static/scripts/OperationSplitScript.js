//Разделение карточек -->
// Добавляем обработчик правого клика на карточки операций
import {filterOperations} from "./FilterScript.js";
import {updateAvailableOperations} from "./MainScript.js";

export function initOperationSplitting() {
    document.addEventListener('contextmenu', function(e) {
        const operationCard = e.target.closest('.operation-card');
        if (!operationCard) return;

        e.preventDefault();
        showSplitDialog(operationCard);
    });
}

// Диалог разделения операции
async function showSplitDialog(operationCard) {
    const currentSearchValue = document.getElementById('operations-search').value;

    const dialog = document.createElement('div');
    dialog.className = 'split-dialog';
    const totalTime = parseFloat(operationCard.dataset.operationTime);

    dialog.innerHTML = `
        <div class="dialog-content">
            <h3>Разделить операцию</h3>
            <p>Общее время: ${totalTime.toFixed(2)} мин</p>
            <div class="split-controls">
                <label>Количество частей:</label>
                <input type="number" id="split-count" min="2" max="10" value="2">
                <button id="split-equal">Равные части</button>
            </div>
            <div id="duration-inputs"></div>
            <div class="dialog-buttons">
                <button id="cancel-split">Отмена</button>
                <button id="confirm-split">Разделить</button>
            </div>
            <div id="error-message" style="color: #ff4444; margin-top: 10px; display: none;"></div>
        </div>
    `;

    document.body.appendChild(dialog);

    // Обновление полей ввода
    const updateInputs = () => {
        const count = parseInt(document.getElementById('split-count').value);
        const durationPerPart = totalTime / count;

        document.getElementById('duration-inputs').innerHTML = '';
        for (let i = 0; i < count; i++) {
            const div = document.createElement('div');
            div.className = 'duration-input-group';
            div.innerHTML = `
                <label>Часть ${i + 1}:</label>
                <input type="number" class="duration-input" value="${durationPerPart.toFixed(2)}" min="0.01" step="0.01">
                <span>мин</span>
            `;
            document.getElementById('duration-inputs').appendChild(div);
        }
    };

    // Проверка соответствия времени
    const validateDurations = () => {
        const inputs = document.querySelectorAll('.duration-input');
        const durations = Array.from(inputs).map(input => parseFloat(input.value));
        const sum = durations.reduce((acc, val) => acc + val, 0);
        const errorElement = document.getElementById('error-message');

        // Допустимая погрешность 0.01 минуты
        if (Math.abs(sum - totalTime) > 0.01) {
            errorElement.textContent = `Сумма частей (${sum.toFixed(2)} мин) не равна исходному времени (${totalTime} мин)`;
            errorElement.style.display = 'block';
            return false;
        }

        errorElement.style.display = 'none';
        return true;
    };

    // Обработчики событий
    document.getElementById('split-count').addEventListener('change', updateInputs);
    document.getElementById('split-equal').addEventListener('click', updateInputs);

    // Проверка при изменении значений
    document.addEventListener('input', (e) => {
        if (e.target.classList.contains('duration-input')) {
            validateDurations();
        }
    });

    document.getElementById('cancel-split').addEventListener('click', () => {
        dialog.remove();
    });

    document.getElementById('confirm-split').addEventListener('click', async () => {
        if (!validateDurations()) {
            return;
        }

        const count = parseInt(document.getElementById('split-count').value);
        const inputs = document.querySelectorAll('.duration-input');
        const durations = Array.from(inputs).map(input => parseFloat(input.value));

        try {
            const response = await fetch(`api/splitOperation/${operationCard.dataset.operationId}?count=${count}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(durations)
            });

            if (!response.ok) throw new Error('Ошибка сервера');

            await updateAvailableOperations();
            document.getElementById('operations-search').value = currentSearchValue;
            filterOperations(); // Применяем сохраненный фильтр

            dialog.remove();

        } catch (error) {
            console.error('Ошибка:', error);
            alert('Не удалось разделить операцию: ' + error.message);
        }
    });

    updateInputs();
}