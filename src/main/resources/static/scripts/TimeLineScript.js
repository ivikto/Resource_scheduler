// TimeLineScript.js
let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);
let zoomLevel = 30; // минут на ячейку
let scheduledOperations = {}; // Объект для хранения операций по ресурсам
let currentResourceId = null; // ID текущего выбранного ресурса

// Инициализация приложения
function initApp() {
    currentDate = new Date();
    loadOperationsFromStorage();
    renderCalendar();
    updateDateDisplay();
    initResourceTabs();
    initMonthNavigation();
    initDragOperations();
    setupSearch();
    setupZoomControls();

    // Инициализация tooltip
    const tooltipContainer = document.createElement('div');
    tooltipContainer.className = 'operation-tooltip';
    document.body.appendChild(tooltipContainer);

    // Отрисовка таймлайна сразу после инициализации
    renderTimeline();
}

/* Календарь */
function initMonthNavigation() {
    document.getElementById('next-month').addEventListener('click', handleNextMonth);
    document.getElementById('prev-month').addEventListener('click', handlePrevMonth);
}

function handleNextMonth() {
    changeMonth(1);
}

function handlePrevMonth() {
    changeMonth(-1);
}

function changeMonth(offset) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + offset,
        1
    );
    renderCalendar();
}

function renderCalendar() {
    const calendarDays = document.getElementById('calendar-days');
    calendarDays.innerHTML = '';

    const monthNames = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];
    document.getElementById('calendar-month-year').textContent =
        `${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;

    const firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    const lastDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
    const daysInMonth = lastDay.getDate();

    let firstDayOfWeek = firstDay.getDay();
    if (firstDayOfWeek === 0) firstDayOfWeek = 7;

    for (let i = 1; i < firstDayOfWeek; i++) {
        const prevMonthDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 0);
        prevMonthDay.setDate(prevMonthDay.getDate() - (firstDayOfWeek - i - 1));

        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        dayElement.textContent = prevMonthDay.getDate();
        calendarDays.appendChild(dayElement);
    }

    for (let i = 1; i <= daysInMonth; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day';
        dayElement.textContent = i;

        if (i === currentDate.getDate() &&
            currentDate.getMonth() === firstDay.getMonth() &&
            currentDate.getFullYear() === firstDay.getFullYear()) {
            dayElement.classList.add('current');
        }

        dayElement.addEventListener('click', () => selectDate(i));
        calendarDays.appendChild(dayElement);
    }

    const totalCells = Math.ceil((firstDayOfWeek - 1 + daysInMonth) / 7) * 7;
    const remainingCells = totalCells - (firstDayOfWeek - 1 + daysInMonth);

    for (let i = 1; i <= remainingCells; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        dayElement.textContent = i;
        calendarDays.appendChild(dayElement);
    }
}

function selectDate(day) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        day
    );
    updateDateDisplay();
    renderTimeline();
}

function updateDateDisplay() {
    const daysOfWeek = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"];

    document.getElementById('current-date').textContent =
        `${daysOfWeek[currentDate.getDay()]}, ${currentDate.getDate()} ${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;
}

/* Таймлайн */
function initResourceTabs() {
    const tabs = document.querySelectorAll('.resource-tab');
    const timelines = document.querySelectorAll('.resource-timeline');

    if (tabs.length === 0) return;

    // Если нет сохраненного ресурса, выбираем первый
    if (!currentResourceId && tabs.length > 0) {
        currentResourceId = tabs[0].getAttribute('data-resource-id');
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

    // Если не нашли активный ресурс, выбираем первый
    if (!foundActive && tabs.length > 0) {
        currentResourceId = tabs[0].getAttribute('data-resource-id');
        tabs[0].classList.add('active');
        const firstTimeline = document.querySelector(`.resource-timeline[data-resource-id="${currentResourceId}"]`);
        if (firstTimeline) {
            firstTimeline.classList.add('active');
        }
    }

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
            saveOperationsToStorage();
            renderTimeline();
        });
    });
}

function renderTimeline() {
    // Если нет активного таймлайна, выбираем первый
    const activeTimeline = document.querySelector('.resource-timeline.active');
    if (!activeTimeline) {
        const firstTimeline = document.querySelector('.resource-timeline');
        if (firstTimeline) {
            firstTimeline.classList.add('active');
            currentResourceId = firstTimeline.getAttribute('data-resource-id');
            const tab = document.querySelector(`.resource-tab[data-resource-id="${currentResourceId}"]`);
            if (tab) tab.classList.add('active');
            return renderTimeline();
        }
        return;
    }

    renderTimeRuler(activeTimeline);
    renderTimeSlots(activeTimeline);
    renderScheduledOperations(activeTimeline);
}

function renderTimeRuler(timelineContainer) {
    const timeRuler = timelineContainer.querySelector('.time-ruler');
    if (!timeRuler) return;

    timeRuler.innerHTML = '';
    timeRuler.style.width = `${24 * 120}px`;

    for (let hour = 0; hour < 24; hour++) {
        const hourElement = document.createElement('div');
        hourElement.className = 'time-ruler-hour';
        hourElement.style.left = `${hour * 120}px`;
        hourElement.style.width = `120px`;

        const hourLabel = document.createElement('div');
        hourLabel.className = 'time-ruler-hour-label';
        hourLabel.textContent = `${hour.toString().padStart(2, '0')}:00`;
        hourLabel.style.left = `40px`;

        hourElement.appendChild(hourLabel);
        timeRuler.appendChild(hourElement);
    }
}

function renderTimeSlots(timelineContainer) {
    const timeSlots = timelineContainer.querySelector('.time-slots');
    if (!timeSlots) return;

    timeSlots.innerHTML = '';
    timeSlots.style.width = `${24 * 120}px`;

    for (let hour = 0; hour < 24; hour++) {
        for (let slot = 0; slot < 6; slot++) {
            const slotElement = document.createElement('div');
            slotElement.className = 'time-slot';
            slotElement.style.left = `${hour * 120 + slot * 20}px`;
            slotElement.style.width = `20px`;
            timeSlots.appendChild(slotElement);
        }
    }
}

function renderScheduledOperations(timelineContainer) {
    try {
        const timeSlots = timelineContainer.querySelector('.time-slots');
        if (!timeSlots) return;

        timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

        const operationsForResource = scheduledOperations[currentResourceId] || [];
        const tooltipContainer = document.querySelector('.operation-tooltip');

        operationsForResource.forEach((op, index) => {
            if (!op || !op.start) return;

            const opStart = new Date(op.start);
            if (opStart.toDateString() !== currentDate.toDateString()) return;

            const durationMinutes = Number(op.durationMinutes) || Number(op.time) * 60 || 0;
            const startMinutes = opStart.getHours() * 60 + opStart.getMinutes();
            const left = (startMinutes / 10) * 20;
            const width = (durationMinutes / 10) * 20;

            if (isNaN(left) || isNaN(width)) return;

            const operationEl = document.createElement('div');
            operationEl.className = 'operation';
            operationEl.style.left = `${left}px`;
            operationEl.style.width = `${width}px`;
            operationEl.dataset.index = index;
            operationEl.dataset.resourceId = currentResourceId;

            operationEl.dataset.tooltip = `
Номер ЗНП: ${op.number || 'Без номера'}
Название: ${op.name || 'Без названия'}
Время: ${(durationMinutes/60).toFixed(1)} ч
Начало: ${opStart.getHours().toString().padStart(2, '0')}:${opStart.getMinutes().toString().padStart(2, '0')}
Номенклатура: ${op.nomenclatureName || 'Без названия'}
            `.trim();

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
            opText.textContent = `${op.name || 'Без названия'} (${formatDuration(durationMinutes)})`;

            operationEl.append(deleteBtn, opText);
            timeSlots.appendChild(operationEl);

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

            makeDraggable(operationEl, index);
        });
    } catch (error) {
        console.error('Error in renderScheduledOperations:', error);
    }
}

function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    const formattedMins = mins > 0 ? mins.toFixed(0) + 'м' : '';

    return `${hours > 0 ? hours + 'ч ' : ''}${formattedMins}`;
}

function initDragOperations() {
    // Обработчики для карточек операций
    document.querySelectorAll('.operation-card').forEach(card => {
        card.addEventListener('dragstart', function(e) {
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

        card.addEventListener('dragend', function() {
            this.classList.remove('dragging');
        });
    });

    // Обработчики для областей таймлайнов
    document.querySelectorAll('.time-slots').forEach(timeSlots => {
        timeSlots.addEventListener('dragover', function(e) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'copy';
            this.classList.add('drag-over');
        });

        timeSlots.addEventListener('dragleave', function() {
            this.classList.remove('drag-over');
        });

        timeSlots.addEventListener('drop', function(e) {
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

function makeDraggable(element, index) {
    const SLOT_WIDTH = 20;
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
            const width = parseFloat(element.style.width);

            newLeft = Math.max(0, Math.min(newLeft, 24 * 6 * SLOT_WIDTH - width));
            newLeft = Math.round(newLeft / SLOT_WIDTH) * SLOT_WIDTH;

            element.style.left = `${newLeft}px`;
        };

        const upHandler = function() {
            if (!isDragging) return;

            document.removeEventListener('mousemove', moveHandler);
            document.removeEventListener('mouseup', upHandler);

            const newLeft = parseFloat(element.style.left);
            const startMinutes = (newLeft / SLOT_WIDTH) * 10;

            const resourceId = element.dataset.resourceId;
            if (scheduledOperations[resourceId] && scheduledOperations[resourceId][index]) {
                const op = scheduledOperations[resourceId][index];
                const newStart = new Date(op.start);
                newStart.setHours(Math.floor(startMinutes / 60), startMinutes % 60);
                op.start = newStart.toISOString();
            }

            element.style.zIndex = '';
            element.classList.remove('dragging');

            saveOperationsToStorage();
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
            id: operationData.id || Date.now(),
            name: operationData.name,
            number: operationData.numberZnp || 'Без номера',
            nomenclatureName: operationData.nomenclatureName || 'Без номенклатуры',
            time: durationMinutes / 60,
            durationMinutes: durationMinutes,
            start: startDate.toISOString(),
            end: new Date(startDate.getTime() + durationMinutes * 60000).toISOString()
        };

        // Инициализируем массив операций для ресурса, если его нет
        if (!scheduledOperations[currentResourceId]) {
            scheduledOperations[currentResourceId] = [];
        }

        scheduledOperations[currentResourceId].push(newOperation);
        saveOperationsToStorage();
        renderTimeline();

        await addInTimeLine(operationData.id);
    } catch (error) {
        console.error('Ошибка при планировании операции:', error);
        alert(error.message);
    }
}

async function addInTimeLine(id) {
    try {
        const response = await fetch(`/api/addInTimeLine/${id}`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
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

async function delFromTimeLine(id) {
    try {
        const response = await fetch(`/api/delFromTimeLine/${id}`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
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

function setupZoomControls() {
    document.getElementById('zoom-slider').addEventListener('input', function(e) {
        zoomLevel = parseInt(e.target.value);
        document.getElementById('zoom-value').textContent = `${zoomLevel} мин`;
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
    clearSearchBtn.addEventListener('click', function() {
        searchInput.value = '';
        filterOperations();
    });
}

function saveOperationsToStorage() {
    localStorage.setItem('scheduledOperations', JSON.stringify(scheduledOperations));
    localStorage.setItem('currentDate', currentDate.toISOString());
    localStorage.setItem('currentResourceId', currentResourceId);
}

function loadOperationsFromStorage() {
    const savedOperations = localStorage.getItem('scheduledOperations');
    const savedDate = localStorage.getItem('currentDate');
    const savedResourceId = localStorage.getItem('currentResourceId');

    if (savedOperations) {
        scheduledOperations = JSON.parse(savedOperations);
    }

    if (savedDate) {
        currentDate = new Date(savedDate);
    }

    if (savedResourceId) {
        currentResourceId = savedResourceId;
    }
}

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
            saveOperationsToStorage();
        }

        await delFromTimeLine(id);

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
            saveOperationsToStorage();
        }

        if (operationEl) {
            operationEl.style.opacity = '1';
            operationEl.style.transform = 'scale(1)';
        }

        alert('Не удалось удалить операцию: ' + error.message);
    }
}

// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', initApp);