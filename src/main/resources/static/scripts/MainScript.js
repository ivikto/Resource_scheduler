import {initMonthNavigation, renderCalendar, updateDateDisplay} from './CalendarScript.js';

// MainScript.js
export let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);
let zoomLevel = 10; // минут на ячейку
let scheduledOperations = {}; // Объект для хранения операций по ресурсам
export let currentResourceId = null; // ID текущего выбранного ресурса
// Глобальные переменные для хранения выбранной операции
let selectedOperationId = null;
let selectedOperationElement = null;

// Инициализация приложения
async function initApp() {
    const today = new Date();
    currentDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    //loadOperationsFromStorage();
    const loadedData = await loadOperationsFromBackend();
    if (loadedData) {
        scheduledOperations = loadedData.operations;
        console.log('Loaded operations:', scheduledOperations); // Проверка данных
    }
    renderCalendar();
    updateDateDisplay();
    initMonthNavigation();

    initResourceTabs();

    initDragOperations();
    initContextMenu();
    setupZoomControls();
    setupOperationForm();

    await updateAvailableOperations()
    timlineScrollWidth()


    // Создаем контейнер для подсказок
    const tooltipContainer = document.createElement('div');
    tooltipContainer.className = 'operation-tooltip';
    document.body.appendChild(tooltipContainer);

    // Отрисовка таймлайна сразу после инициализации
    renderTimeline();
}

/* Таймлайн */
export function initResourceTabs() {
    const tabs = document.querySelectorAll('.resource-tab');
    const timelines = document.querySelectorAll('.resource-timeline');

    if (tabs.length === 0) return;

    // Получаем сохраненный resourceId из localStorage
    const savedResourceId = localStorage.getItem('currentResourceId');

    // Устанавливаем текущий ресурс (из сохраненного или первый)
    if (savedResourceId && Array.from(tabs).some(tab => tab.getAttribute('data-resource-id') === savedResourceId)) {
        currentResourceId = savedResourceId;
    } else if (tabs.length > 0) {
        currentResourceId = tabs[0].getAttribute('data-resource-id');
        // Сохраняем первый ресурс по умолчанию
        localStorage.setItem('currentResourceId', currentResourceId);
    }

    // Активируем соответствующую вкладку и таймлайн
    let foundActive = false;
    tabs.forEach(tab => {
        const resourceId = tab.getAttribute('data-resource-id');
        if (resourceId === currentResourceId) {
            tab.classList.add('active');
            const timeline = document.querySelector(`.resource-timeline[data-resource-id="${resourceId}"]`);
            if (timeline) {
                timeline.classList.add('active');
                foundActive = true;
            }
        }
    });

    // Обработчики кликов по вкладкам
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const resourceId = this.getAttribute('data-resource-id');
            if (resourceId === currentResourceId) return;

            tabs.forEach(t => t.classList.remove('active'));
            timelines.forEach(t => t.classList.remove('active'));

            this.classList.add('active');
            document.querySelector(`.resource-timeline[data-resource-id="${resourceId}"]`).classList.add('active');

            currentResourceId = resourceId;
            // Сохраняем выбранный ресурс
            localStorage.setItem('currentResourceId', currentResourceId);

            renderTimeline();
        });
    });
}

export function renderTimeline() {
    const activeTimeline = document.querySelector('.resource-timeline.active');
    if (!activeTimeline) {
        const firstTimeline = document.querySelector('.resource-timeline');
        if (firstTimeline) {
            firstTimeline.classList.add('active');
            currentResourceId = firstTimeline.getAttribute('data-resource-id');
            return renderTimeline();
        }
        return;
    }

    // Базовый размер ячейки (при zoomLevel = 10)
    const baseCellWidth = 20;
    // Текущий размер ячейки
    const cellWidth = baseCellWidth * (10 / zoomLevel);

    renderTimeRuler(activeTimeline, cellWidth);
    renderTimeSlots(activeTimeline, cellWidth);
    renderScheduledOperations(activeTimeline, cellWidth);
}

export function renderTimeRuler(timelineContainer, cellWidth) {
    const timeRuler = timelineContainer.querySelector('.time-ruler');
    if (!timeRuler) return;

    timeRuler.innerHTML = '';
    // 24 часа * 6 ячеек в часе (при базовом zoomLevel = 10)
    timeRuler.style.width = `${24 * 6 * cellWidth}px`;

    for (let hour = 0; hour < 24; hour++) {
        const hourElement = document.createElement('div');
        hourElement.className = 'time-ruler-hour';
        hourElement.style.left = `${hour * 6 * cellWidth}px`;
        hourElement.style.width = `${6 * cellWidth}px`;


        const hourLabel = document.createElement('div');
        hourLabel.className = 'time-ruler-hour-label';
        hourLabel.textContent = `${hour.toString().padStart(2, '0')}:00`;
        hourLabel.style.alignItems = 'center'
        hourLabel.style.display = 'flex'
        hourLabel.style.justifyContent = 'center'
        hourElement.appendChild(hourLabel);
        timeRuler.appendChild(hourElement);
    }
}
export function renderTimeSlots(timelineContainer, cellWidth) {
    const timeSlots = timelineContainer.querySelector('.time-slots');
    if (!timeSlots) return;

    timeSlots.innerHTML = '';
    timeSlots.style.width = `${24 * 6 * cellWidth}px`;

    // 24 часа * 6 ячеек в часе
    for (let i = 0; i < 24 * 6; i++) {
        const slot = document.createElement('div');
        slot.className = 'time-slot';
        slot.style.left = `${i * cellWidth}px`;
        slot.style.width = `${cellWidth}px`;
        timeSlots.appendChild(slot);
    }
}


export function renderScheduledOperations(timelineContainer, cellWidth) {
    const timeSlots = timelineContainer.querySelector('.time-slots');
    if (!timeSlots) return;

    // Удаляем старые операции
    timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

    const operationsForResource = scheduledOperations[currentResourceId] || [];
    const tooltipContainer = document.querySelector('.operation-tooltip');

    operationsForResource.forEach((op, index) => {
        if (!op || !op.start) return;

        const opStart = new Date(op.start);
        if (opStart.toDateString() !== currentDate.toDateString()) return;

        const durationMinutes = Number(op.durationMinutes) || Number(op.time) * 60 || 0;
        const startMinutes = opStart.getHours() * 60 + opStart.getMinutes();

        // Расчет позиции и ширины с учетом масштаба
        const left = (startMinutes / 10) * cellWidth; // 10 - базовый zoom (10 минут)
        const width = (durationMinutes / 10) * cellWidth;

        if (isNaN(left) || isNaN(width)) return;

        const operationEl = document.createElement('div');
        const isManual = String(op.id).startsWith('manual') || op.id === 'manual';
        operationEl.className = isManual ? 'operation manual' : 'operation';
        operationEl.style.left = `${left}px`;
        operationEl.style.width = `${width}px`;
        operationEl.dataset.index = index;
        operationEl.dataset.resourceId = currentResourceId;

        // Форматирование данных для подсказки
        const formattedDuration = parseFloat(formatDuration(durationMinutes)).toFixed(2);
        const startTime = `${opStart.getHours().toString().padStart(2, '0')}:${opStart.getMinutes().toString().padStart(2, '0')}`;

        operationEl.dataset.tooltip = `
            Номер ЗНП: ${op.number || 'Без номера'}
            Название: ${op.name || 'Без названия'}
            Время: ${formattedDuration}
            Начало: ${startTime}
            Номенклатура: ${op.nomenclatureName || 'Без названия'}
        `.trim();

        // Создаем элементы операции
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'delete-operation-btn';
        deleteBtn.innerHTML = '×';
        deleteBtn.dataset.index = index;
        deleteBtn.dataset.resourceId = currentResourceId;
        deleteBtn.onclick = function(e) {
            e.stopPropagation();
            const idx = parseInt(this.dataset.index);
            if (!isNaN(idx)) deleteOperation(idx, op.id);
        };

        const opText = document.createElement('span');
        opText.className = 'operation-text';
        opText.textContent = `${op.name || 'Без названия'} (${formattedDuration}) ч.`;

        // Добавляем элементы в операцию
        operationEl.append(opText);
        timeSlots.appendChild(operationEl);

        // Обработчики для подсказки
        operationEl.addEventListener('mouseenter', function(e) {
            if (!tooltipContainer) return;
            const rect = this.getBoundingClientRect();
            tooltipContainer.textContent = this.dataset.tooltip;
            tooltipContainer.style.left = `${rect.left}px`;
            tooltipContainer.style.top = `${rect.top - tooltipContainer.offsetHeight - 5}px`;
            tooltipContainer.style.visibility = 'visible';
            tooltipContainer.style.opacity = '1';
        });

        operationEl.addEventListener('mouseleave', function() {
            if (!tooltipContainer) return;
            tooltipContainer.style.visibility = 'hidden';
            tooltipContainer.style.opacity = '0';
        });
        operationEl.addEventListener('contextmenu', function(e) {
            e.preventDefault();
            // Данные сохранятся в глобальных переменных при показе меню
        });

        // Делаем операцию перетаскиваемой
        makeDraggable(operationEl, index, cellWidth);
    });
}

export function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours > 0 ? hours + 'ч ' : ''}${mins > 0 ? mins + 'м' : ''}`.trim();
}

/* Таймлайн */


export function initDragOperations() {
    // Обработчики для карточек операций
    document.querySelectorAll('.operation-card').forEach(card => {
        card.addEventListener('dragstart', function (e) {
            const timeMinutes = parseFloat(this.dataset.operationTime);
            const timeHours = timeMinutes / 60;

            const opData = {
                id: this.dataset.operationId,
                name: this.dataset.operationName,
                numberZnp: this.dataset.operationNumber,
                nomenclatureName: this.dataset.operationNomenclature,
                time: timeHours,
                durationMinutes: timeMinutes
            };

            e.dataTransfer.setData('application/json', JSON.stringify(opData));
            this.classList.add('dragging');
        });

        card.addEventListener('dragend', function () {
            this.classList.remove('dragging');
        });
    });

    // Обработчики для областей таймлайнов
    document.querySelectorAll('.time-slots').forEach(timeSlots => {
        timeSlots.addEventListener('dragover', function (e) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'copy';
            this.classList.add('drag-over');
        });

        timeSlots.addEventListener('dragleave', function () {
            this.classList.remove('drag-over');
        });

        timeSlots.addEventListener('drop', function (e) {
            e.preventDefault();
            this.classList.remove('drag-over');

            try {
                const jsonData = e.dataTransfer.getData('application/json');
                if (!jsonData) throw new Error('Нет данных операции');

                const operationData = JSON.parse(jsonData);
                if (!operationData.time || !operationData.name) {
                    throw new Error('Неполные данные операции');
                }

                const rect = this.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const minutes = Math.max(0, Math.round(x / (120 / 60)));

                scheduleOperation(operationData, minutes);
            } catch (error) {
                console.error('Ошибка при обработке операции:', error);
                alert(error.message);
            }
        });
    });
}

function makeDraggable(element, index, cellWidth) {
    let isDragging = false;
    let startX, startLeft;

    element.addEventListener('mousedown', function(e) {
        if (e.target.classList.contains('delete-operation-btn')) return;

        isDragging = true;
        startX = e.clientX;
        startLeft = parseFloat(element.style.left);
        element.style.zIndex = '1000';
        element.classList.add('dragging');

        const moveHandler = function(e) {
            if (!isDragging) return;
            const dx = e.clientX - startX;
            let newLeft = startLeft + dx;

            // Ограничиваем перемещение границами таймлайна
            newLeft = Math.max(0, Math.min(newLeft, 24 * 6 * cellWidth - parseFloat(element.style.width)));

            // Привязка к сетке времени
            newLeft = Math.round(newLeft / cellWidth) * cellWidth;

            element.style.left = `${newLeft}px`;
        };

        const upHandler = function() {
            if (!isDragging) return;
            document.removeEventListener('mousemove', moveHandler);
            document.removeEventListener('mouseup', upHandler);

            const newLeft = parseFloat(element.style.left);
            const startMinutes = (newLeft / cellWidth) * 10; // 10 - базовый zoom

            // Обновляем время операции
            const resourceId = element.dataset.resourceId;
            if (scheduledOperations[resourceId] && scheduledOperations[resourceId][index]) {
                const op = scheduledOperations[resourceId][index];
                const newStart = new Date(op.start);
                newStart.setHours(Math.floor(startMinutes / 60), startMinutes % 60);
                op.start = newStart.toISOString();
                //saveOperationsToStorage();
                saveOperationsToBackend();
            }

            element.style.zIndex = '';
            element.classList.remove('dragging');
            isDragging = false;
        };

        document.addEventListener('mousemove', moveHandler);
        document.addEventListener('mouseup', upHandler, { once: true });
        e.preventDefault();
    });
}

async function scheduleOperation(operationData, startMinutes) {
    try {
        const durationMinutes = operationData.durationMinutes || (operationData.time * 60);
        const endMinutes = startMinutes + durationMinutes;

        if (endMinutes > 24 * 60) {
            throw new Error('Операция не может выходить за пределы 24 часов');
        }

        const startDate = new Date(
            currentDate.getFullYear(),
            currentDate.getMonth(),
            currentDate.getDate(),
            Math.floor(startMinutes / 60),
            startMinutes % 60
        );

        const newOperation = {
            id: operationData.id || 'manual',
            name: operationData.name,
            number: operationData.numberZnp || 'Без номера',
            nomenclatureName: operationData.nomenclatureName || 'Без номенклатуры',
            time: durationMinutes / 60,
            durationMinutes: durationMinutes,
            start: startDate.toISOString(),
            end: new Date(startDate.getTime() + durationMinutes * 60000).toISOString()
        };

        // Инициализация массива операций для ресурса, если он не существует
        if (!scheduledOperations[currentResourceId]) {
            scheduledOperations[currentResourceId] = []; // Вот ключевое исправление
        }

        // Проверяем, что scheduledOperations[currentResourceId] является массивом
        if (!Array.isArray(scheduledOperations[currentResourceId])) {
            scheduledOperations[currentResourceId] = [];
        }

        scheduledOperations[currentResourceId].push(newOperation);
        //saveOperationsToStorage();
        await saveOperationsToBackend();
        renderTimeline();

        await addInTimeLine(operationData.id);
        await updateAvailableOperations(); // Обновляем список операций
    } catch (error) {
        console.error('Ошибка при планировании операции:', error);
        alert(error.message);
    }
}

async function addInTimeLine(id) {
    console.log(id)
    if (id !== 'manual') {
        try {
            const response = await fetch(`/api/addInTimeLine/${id}`, {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Server error: ${response.status} - ${errorText}`);
            }
        } catch (error) {
            console.error('Ошибка при сохранении на сервере:', error);
            throw error;
        }
    }

}

async function delFromTimeLine(id) {
    console.log(id)
    if (id !== 'manual') {
        try {
            const response = await fetch(`/api/delFromTimeLine/${id}`, {
                method: 'GET',
                headers: {'Accept': 'application/json'}
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Ошибка сервера: ${response.status} - ${errorText}`);
            }
        } catch (error) {
            console.error('Ошибка при удалении из timeline:', error);
            throw error;
        }
    }

}

export function setupZoomControls() {
    const zoomSlider = document.getElementById('zoom-slider');
    const zoomValue = document.getElementById('zoom-value');

    // Настройки слайдера (5, 10, 15, 20 минут)
    zoomSlider.min = 5;
    zoomSlider.max = 20;
    zoomSlider.step = 5;
    zoomSlider.value = zoomLevel;
    zoomValue.textContent = `${zoomLevel} мин`;

    zoomSlider.addEventListener('input', function(e) {
        zoomLevel = parseInt(e.target.value);
        zoomValue.textContent = `${zoomLevel} мин`;
        renderTimeline();
    });
}


function setupSearch() {
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

/* Сохранение и загрузка операций таймлайна в бд --> */
async function saveOperationsToBackend() {
    const data = {
        resourceId: currentResourceId,
        operations: JSON.stringify(scheduledOperations)
    };

    try {
        const response = await fetch('api/save-operations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        return await response.json();
    } catch (error) {
        console.error('Save failed:', error);
        return { error: error.message };
    }
}

async function loadOperationsFromBackend(resourceId = null) {
    const url = resourceId
        ? `api/load-operations?resourceId=${encodeURIComponent(resourceId)}`
        : 'api/load-operations';

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const operations = await response.json();

        if (!operations?.length) {
            console.warn('No operations received');
            return null;
        }

        const latest = operations[operations.length - 1];
        //console.log('Latest operation data:', latest); // 4

        const parsedData = {
            operations: JSON.parse(latest.operations),
            date: latest.operationDate ? new Date(latest.operationDate) : null,
            resourceId: latest.resourceId
        };

        console.log('Parsed data:', parsedData); // 5
        return parsedData;
    } catch (error) {
        console.error('Load failed:', error);
        return null;
    }
}

/* <-- Сохранение операций таймлайна в бд */

async function deleteOperation(index, id) {
    if (index === undefined || index === null || isNaN(index)) {
        console.error('Invalid index:', index);
        return;
    }

    if (!confirm('Вы действительно хотите удалить эту операцию?')) {
        return;
    }

    const operationEl = document.querySelector(`.operation[data-index="${index}"][data-resource-id="${currentResourceId}"]`);
    let deletedOperation = null;

    try {
        if (operationEl) {
            operationEl.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            operationEl.style.opacity = '0';
            operationEl.style.transform = 'scale(0.95)';
        }

        if (scheduledOperations[currentResourceId] && scheduledOperations[currentResourceId][index]) {
            deletedOperation = scheduledOperations[currentResourceId][index];
            scheduledOperations[currentResourceId].splice(index, 1);
            //saveOperationsToStorage();
            await saveOperationsToBackend();
        }

        await delFromTimeLine(id);
        await updateAvailableOperations(); // Обновляем список операций

        if (operationEl?.parentNode) {
            setTimeout(() => {
                operationEl.parentNode.removeChild(operationEl);
                renderTimeline();
            }, 300);
        } else {
            renderTimeline();
        }
    } catch (error) {
        console.error('Ошибка при удалении операции:', error);

        if (deletedOperation && scheduledOperations[currentResourceId]) {
            scheduledOperations[currentResourceId].splice(index, 0, deletedOperation);
            //saveOperationsToStorage();
            await saveOperationsToBackend();
        }

        if (operationEl) {
            operationEl.style.opacity = '1';
            operationEl.style.transform = 'scale(1)';
        }

        alert('Не удалось удалить операцию: ' + error.message);
    }
}

//Форма ручного добавления операции -->
// Настройка формы добавления операции
export function setupOperationForm() {
    // Устанавливаем текущую дату по умолчанию
    const dateInput = document.getElementById('operation-date');
    dateInput.valueAsDate = new Date();

    const addBtn = document.getElementById('add-operation');
    addBtn.addEventListener('click', addManualOperationToTimeline);
}

// Функция добавления ручной операции в таймлайн
function addManualOperationToTimeline() {
    // Получаем значения из формы
    const name = document.getElementById('new-operation-name').value.trim();
    const duration = parseInt(document.getElementById('new-operation-duration').value);
    const startTime = document.getElementById('operation-start').value;
    const operationDate = document.getElementById('operation-date').value;

    // Валидация
    if (!name) {
        alert('Укажите название операции');
        return;
    }

    if (isNaN(duration) || duration < 5) {
        alert('Длительность должна быть не менее 5 минут');
        return;
    }

    if (!startTime) {
        alert('Укажите время начала');
        return;
    }

    if (!operationDate) {
        alert('Укажите дату операции');
        return;
    }

    // Парсим дату и время
    const [year, month, day] = operationDate.split('-');
    const [hours, minutes] = startTime.split(':');
    const startDate = new Date(year, month - 1, day, hours, minutes);

    // Создаем объект операции с оранжевым цветом
    const operationData = {
        id: 'manual',
        name: name,
        durationMinutes: duration,
        number: 'Ручная операция',
        nomenclatureName: 'Ручное добавление',
        time: duration / 60,
        color: '#FFA500' // Оранжевый цвет для ручных операций
    };

    // Добавляем операцию в таймлайн
    scheduleManualOperation(operationData, startDate);

    // Очищаем форму (кроме даты)
    document.getElementById('new-operation-name').value = '';
    document.getElementById('new-operation-duration').value = '30';
    document.getElementById('operation-start').value = '';
}

async function scheduleManualOperation(operationData, startDate) {
    const durationMinutes = operationData.durationMinutes;
    const endDate = new Date(startDate.getTime() + durationMinutes * 60000);

    const newOperation = {
        ...operationData,
        start: startDate.toISOString(),
        end: endDate.toISOString()
    };

    // Сохраняем в scheduledOperations
    if (!scheduledOperations[currentResourceId]) {
        scheduledOperations[currentResourceId] = [];
    }
    scheduledOperations[currentResourceId].push(newOperation);

    //saveOperationsToStorage();
    await saveOperationsToBackend();
    renderTimeline();
}

async function updateAvailableOperations() {
    try {
        const searchValue = document.getElementById('operations-search').value;
        const response = await fetch('api/operations');
        if (!response.ok) throw new Error('Ошибка при загрузке операций');

        const operations = await response.json();
        renderAvailableOperations(operations);

        // Восстанавливаем фильтр если он был
        if (searchValue) {
            document.getElementById('operations-search').value = searchValue;
            filterOperations();
        }
    } catch (error) {
        console.error('Ошибка обновления операций:', error);
    }
}

function renderAvailableOperations(operations) {
    const operationsList = document.querySelector('.operations-list');
    operationsList.innerHTML = '';

    operations.forEach(op => {
        const card = document.createElement('div');
        card.className = 'operation-card';
        card.draggable = true;
        card.dataset.operationId = op.id;
        card.dataset.operationName = op.name;
        card.dataset.operationTime = op.time;
        card.dataset.operationNumber = op.number;
        card.dataset.operationNomenclature = op.nomenclatureName;

        // Форматируем время с одним десятичным знаком
        const formattedTime = parseFloat(op.time).toFixed(1);

        // Устанавливаем градиентный фон
        card.style.background = `linear-gradient(to right, ${op.color} 5px, #f5f5f5 5px)`;

        // Формируем текст подсказки с переносами строк
        card.dataset.tooltip = `Номер ЗНП: ${op.number || 'Без номера'}\n` +
            `Операция: ${op.name || 'Без названия'}\n` +
            `Время: ${formattedTime} мин\n` +
            `Номенклатура: ${op.nomenclatureName || 'Без названия'}`;

        // Создаем содержимое карточки
        const contentSpan = document.createElement('span');
        contentSpan.textContent = `${op.name} (${formattedTime} мин)`;

        const deleteBtn = document.createElement('span');
        deleteBtn.className = 'delete-btn';
        deleteBtn.dataset.id = op.id;
        deleteBtn.textContent = '×';

        // Добавляем элементы в карточку
        card.appendChild(contentSpan);
        card.appendChild(deleteBtn);

        // Добавляем обработчик удаления
        deleteBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            deleteOperationFromList(op.id);
        });

        // Добавляем обработчики drag-and-drop
        card.addEventListener('dragstart', function (e) {
            e.dataTransfer.setData('application/json', JSON.stringify({
                id: op.id,
                name: op.name,
                numberZnp: op.number,
                nomenclatureName: op.nomenclatureName,
                time: op.time,
                durationMinutes: op.time,
                color: op.color
            }));
            this.classList.add('dragging');
        });

        card.addEventListener('dragend', function () {
            this.classList.remove('dragging');
        });

        operationsList.appendChild(card);
    });
    setupSearch();
}

async function deleteOperationFromList(operationId) {
    console.log('delete')
    if (confirm('Вы действительно хотите удалить эту операцию?')) {
        try {
            const response = await fetch(`/api/delete/${operationId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                updateAvailableOperations(); // Обновляем список
            } else {
                throw new Error('Ошибка при удалении операции');
            }
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Не удалось удалить операцию');
        }
    }
}

//Разделение карточек -->
// Добавляем обработчик правого клика на карточки операций
function initOperationSplitting() {
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

function filterOperations() {
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
// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', initOperationSplitting);
//<-- Разделение карточек

// Меню удаления с таймлайна -->
// Инициализация контекстного меню
export function initContextMenu() {
    const contextMenu = document.getElementById('context-menu');
    const deleteOption = document.getElementById('delete-operation');

    document.addEventListener('contextmenu', function(e) {
        const operationElement = e.target.closest('.operation');
        if (!operationElement) return;

        e.preventDefault();

        // Получаем ID операции и проверяем его
        const opId = operationElement.dataset.operationId;

        console.log(operationElement.dataset)
        console.log(operationElement.dataset.operationId)

        // Проверяем, что ID существует и валиден
        if (!opId || opId === "undefined") {
            console.error('Invalid operation ID:', opId);
            alert('Неверный ID операции');
            return;
        }

        // Сохраняем выбранные данные
        selectedOperationId = opId;
        selectedOperationElement = operationElement;

        // Позиционируем меню
        contextMenu.style.display = 'block';
        contextMenu.style.left = `${e.pageX}px`;
        contextMenu.style.top = `${e.pageY}px`;
    });

    // Обработчик удаления
    deleteOption.addEventListener('click', function() {
        if (!selectedOperationId) {
            alert('Операция не выбрана');
            return;
        }

        if (confirm('Вы уверены, что хотите удалить эту операцию?')) {
            deleteOperationFromTimeline(selectedOperationId, selectedOperationElement);
        }
        hideContextMenu();
    });

    // Скрытие меню
    document.addEventListener('click', function(e) {
        if (e.button !== 2 && !contextMenu.contains(e.target)) {
            hideContextMenu();
        }
    });
}

function hideContextMenu() {
    const contextMenu = document.getElementById('context-menu');
    contextMenu.style.display = 'none';
    selectedOperationId = null;
    selectedOperationElement = null;
}

async function deleteOperationFromTimeline(operationId, operationElement) {
    // Проверяем ID операции
    if (!operationId || operationId === "undefined") {
        alert('Неверный ID операции');
        return;
    }

    try {
        // Анимация удаления
        operationElement.style.transition = 'opacity 0.3s, transform 0.3s';
        operationElement.style.opacity = '0';
        operationElement.style.transform = 'scale(0.9)';

        // Отправка запроса на сервер
        const response = await fetch(`/api/deleteFromTimeLine/${operationId}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                resourceId: currentResourceId
            })
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        // Удаление элемента после анимации
        setTimeout(() => {
            operationElement.remove();
            updateAvailableOperations();
        }, 300);

    } catch (error) {
        console.error('Ошибка удаления:', error);

        // Восстанавливаем элемент при ошибке
        operationElement.style.opacity = '1';
        operationElement.style.transform = 'scale(1)';

        alert(error.message || 'Не удалось удалить операцию');
    }
}

// <-- Меню удаления с таймлайна

// Скролл полосы таймлайна -->
function timlineScrollWidth() {
    //console.log('Starting timeline scroll adjustment');

    const MAX_ATTEMPTS = 10;
    let attempts = 0;

    const scrollToCenter = () => {
        const timelineContainer = document.querySelector('.timeline-container');

        if (!timelineContainer) {
            console.error('Timeline container not found');
            return false;
        }

        // Проверяем, есть ли что прокручивать
        if (timelineContainer.scrollWidth <= timelineContainer.clientWidth) {
            //console.log('No scroll needed - content fits container');
            return true;
        }

        const centerPosition = (timelineContainer.scrollWidth / 2 - timelineContainer.clientWidth / 2) + 175;

        console.log(`Attempt ${attempts + 1}:`, {
            scrollWidth: timelineContainer.scrollWidth,
            clientWidth: timelineContainer.clientWidth,
            calculatedPosition: centerPosition
        });

        if (centerPosition > 0 && !isNaN(centerPosition)) {
            timelineContainer.scrollLeft = centerPosition;
            console.log('Successfully scrolled to center');
            return true;
        }

        return false;
    };

    // Пробуем сразу
    if (scrollToCenter()) return;

    // Если не получилось, пробуем с интервалом
    const retryInterval = setInterval(() => {
        attempts++;

        if (scrollToCenter() || attempts >= MAX_ATTEMPTS) {
            clearInterval(retryInterval);
            if (attempts >= MAX_ATTEMPTS) {
                console.error('Failed to scroll after maximum attempts');
            }
        }
    }, 300); // Увеличили интервал между попытками
}

// Вызываем при загрузке и после рендеринга таймлайна
document.addEventListener('DOMContentLoaded', timlineScrollWidth);
window.addEventListener('load', timlineScrollWidth);

// <-- Скролл полосы таймлайна
// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', initApp);