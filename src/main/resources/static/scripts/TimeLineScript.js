// TimeLineScript.js
let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);
let zoomLevel = 30; // минут на ячейку
let scheduledOperations = [];

// Инициализация приложения
function initApp() {
    loadOperationsFromStorage(); // Загружаем сохраненные данные перед рендерингом
    renderCalendar();
    updateDateDisplay();
    renderTimeline();
    initMonthNavigation();
    document.getElementById('time-slots').addEventListener('click', function(e) {
        if (e.target.classList.contains('delete-operation-btn')) {
            e.preventDefault();
            e.stopPropagation();
            const index = parseInt(e.target.dataset.index);
            if (!isNaN(index)) {
                deleteOperation(index);
            }
        }
    });
    document.querySelectorAll('.operation-card').forEach(card => {
        card.addEventListener('mousemove', function(e) {
            this.style.setProperty('--mouse-x', `${e.clientX}px`);
            this.style.setProperty('--mouse-y', `${e.clientY}px`);
        });
    });
    initDragOperations();
}
/* Смена месяцев в календаре */
function initMonthNavigation() {
    const nextMonth = document.getElementById('next-month');
    const prevMonth = document.getElementById('prev-month');

    nextMonth.addEventListener('click', () => {
        handleNextMonth();
        console.log('next month click');
        return;
    });
    prevMonth.addEventListener('click', () => {
        handlePrevMonth();
        console.log('prev month click');
        return;
    });

}

function handleNextMonth() {
    changeMonth(1);
}

function handlePrevMonth() {
    changeMonth(-1);
}

// Переключение месяцев
function changeMonth(offset) {
    console.log(offset)
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + offset,
        1
    );
    renderCalendar();
}

/* Смена месяцев в календаре */

// Отрисовка календаря (остается без изменений)
function renderCalendar() {
    const calendarDays = document.getElementById('calendar-days');


    calendarDays.innerHTML = '';

    // Обновление заголовка календаря
    const monthNames = ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"];
    document.getElementById('calendar-month-year').textContent =
        `${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;

    // Получаем первый день месяца и количество дней
    const firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    const lastDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
    const daysInMonth = lastDay.getDate();

    // Определяем день недели первого дня месяца (0 - воскресенье, 1 - понедельник и т.д.)
    let firstDayOfWeek = firstDay.getDay();
    if (firstDayOfWeek === 0) firstDayOfWeek = 7; // Делаем воскресенье 7-м днем

    // Добавляем пустые ячейки для дней предыдущего месяца
    for (let i = 1; i < firstDayOfWeek; i++) {
        const prevMonthDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 0);
        prevMonthDay.setDate(prevMonthDay.getDate() - (firstDayOfWeek - i - 1));

        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        dayElement.textContent = prevMonthDay.getDate();
        calendarDays.appendChild(dayElement);
    }

    // Добавляем дни текущего месяца
    for (let i = 1; i <= daysInMonth; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day';
        dayElement.textContent = i;

        // Проверяем, является ли день текущим
        if (i === currentDate.getDate() &&
            currentDate.getMonth() === firstDay.getMonth() &&
            currentDate.getFullYear() === firstDay.getFullYear()) {
            dayElement.classList.add('current');
        }

        // Добавляем обработчик клика
        dayElement.addEventListener('click', () => {
            selectDate(i);
        });

        calendarDays.appendChild(dayElement);
    }

    // Добавляем пустые ячейки для дней следующего месяца
    const totalCells = Math.ceil((firstDayOfWeek - 1 + daysInMonth) / 7) * 7;
    const remainingCells = totalCells - (firstDayOfWeek - 1 + daysInMonth);

    for (let i = 1; i <= remainingCells; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        dayElement.textContent = i;
        calendarDays.appendChild(dayElement);
    }
}

// Выбор даты в календаре (остается без изменений)
function selectDate(day) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        day
    );
    updateDateDisplay();
    renderTimeline();
}



// Обновление отображения текущей даты (остается без изменений)
function updateDateDisplay() {
    const daysOfWeek = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"];

    document.getElementById('current-date').textContent =
        `${daysOfWeek[currentDate.getDay()]}, ${currentDate.getDate()} ${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;
}

function renderTimeline() {
    renderTimeRuler();
    renderTimeSlots();
    renderScheduledOperations();
}

// Отрисовка линейки времени с учетом масштаба
function renderTimeRuler() {
    const timeRuler = document.getElementById('time-ruler');
    timeRuler.innerHTML = '';

    // Фиксированная ширина часа (6 слотов по 10 минут = 120px)
    const HOUR_WIDTH = 120;

    // Общая ширина контейнера (24 часа)
    timeRuler.style.width = `${24 * HOUR_WIDTH}px`;

    for (let hour = 0; hour < 24; hour++) {
        const hourElement = document.createElement('div');
        hourElement.className = 'time-ruler-hour';
        hourElement.style.left = `${hour * HOUR_WIDTH}px`;
        hourElement.style.width = `${HOUR_WIDTH}px`;

        const hourLabel = document.createElement('div');
        hourLabel.className = 'time-ruler-hour-label';
        hourLabel.textContent = `${hour.toString().padStart(2, '0')}:00`;
        hourLabel.style.left = `${HOUR_WIDTH / 2 - 20}px`; // Центрируем метку

        hourElement.appendChild(hourLabel);
        timeRuler.appendChild(hourElement);
    }
}

// Отрисовка временных слотов с учетом масштаба
function renderTimeSlots() {
    const timeSlots = document.getElementById('time-slots');
    timeSlots.innerHTML = '';

    // Фиксированная ширина часа (6 слотов по 10 минут = 120px)
    const HOUR_WIDTH = 120;
    const SLOT_WIDTH = HOUR_WIDTH / 6; // 10 минут = 20px

    // Общая ширина контейнера (24 часа)
    timeSlots.style.width = `${24 * HOUR_WIDTH}px`;

    // Создаем слоты (24 часа * 6 слотов = 144 слота)
    for (let hour = 0; hour < 24; hour++) {
        for (let slot = 0; slot < 6; slot++) {
            const slotElement = document.createElement('div');
            slotElement.className = 'time-slot';
            slotElement.style.left = `${hour * HOUR_WIDTH + slot * SLOT_WIDTH}px`;
            slotElement.style.width = `${SLOT_WIDTH}px`;
            timeSlots.appendChild(slotElement);
        }
    }
}

// Отрисовка запланированных операций с правильным позиционированием
function renderScheduledOperations() {
    try {
        const timeSlots = document.getElementById('time-slots');
        if (!timeSlots) return;

        // Создаем общий контейнер для подсказки
        const tooltipContainer = document.createElement('div');
        tooltipContainer.className = 'operation-tooltip';
        document.body.appendChild(tooltipContainer);

        // Очистка предыдущих операций
        timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

        const HOUR_WIDTH = 120;

        scheduledOperations.forEach((op, index) => {
            if (!op || !op.start) return;

            const opStart = new Date(op.start);
            if (opStart.toDateString() !== currentDate.toDateString()) return;

            const durationMinutes = Number(op.durationMinutes) || Number(op.time) * 60 || 0;
            const startMinutes = opStart.getHours() * 60 + opStart.getMinutes();
            const left = (startMinutes / 10) * 20;
            const width = (durationMinutes / 10) * 20;

            if (isNaN(left) || isNaN(width)) return;

            // Создаем элемент операции (без изменений структуры)
            const operationEl = document.createElement('div');
            operationEl.className = 'operation';
            operationEl.style.left = `${left}px`;
            operationEl.style.width = `${width}px`;
            operationEl.dataset.index = index;

            // Добавляем данные для подсказки в dataset
            operationEl.dataset.tooltip = `
Номер ЗНП: ${op.number || 'Без номера'}
Название: ${op.name || 'Без названия'}
Время: ${(durationMinutes/60).toFixed(1)} ч
Начало: ${opStart.getHours().toString().padStart(2, '0')}:${opStart.getMinutes().toString().padStart(2, '0')}
Номенклатура: ${op.nomenclatureName || 'Без названия'}
            `.trim();

            // Кнопка удаления (без изменений)
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'delete-operation-btn';
            deleteBtn.innerHTML = '×';
            deleteBtn.dataset.index = index;
            deleteBtn.onclick = function(e) {
                e.stopPropagation();
                const idx = parseInt(this.dataset.index);
                if (!isNaN(idx)) deleteOperation(idx, op.id);
            };

            // Текст операции
            const opText = document.createElement('span');
            opText.className = 'operation-text';
            opText.textContent = `${op.name || 'Без названия'} (${formatDuration(durationMinutes)})`;

            operationEl.append(deleteBtn, opText);
            timeSlots.appendChild(operationEl);

            // Обработчики для подсказки
            operationEl.addEventListener('mouseenter', function(e) {
                const rect = this.getBoundingClientRect();
                tooltipContainer.textContent = this.dataset.tooltip;
                tooltipContainer.style.left = `${rect.left}px`;
                tooltipContainer.style.top = `${rect.top - tooltipContainer.offsetHeight - 5}px`;
                tooltipContainer.style.visibility = 'visible';
                tooltipContainer.style.opacity = '1';
            });

            operationEl.addEventListener('mouseleave', function() {
                tooltipContainer.style.visibility = 'hidden';
                tooltipContainer.style.opacity = '0';
            });

            makeDraggable(operationEl, index);
        });
    } catch (error) {
        console.error('Error in renderScheduledOperations:', error);
    }
}

// Вспомогательная функция для форматирования длительности
function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    const formattedMins = mins > 0 ? mins.toFixed(0) + 'м' : '';

    return `${hours > 0 ? hours + 'ч ' : ''}${formattedMins}`;
}

// Инициализация перетаскивания
function initDragOperations() {
    const timeSlots = document.getElementById('time-slots');

    // Обработчики для карточек операций
    document.querySelectorAll('.operation-card').forEach(card => {
        card.addEventListener('dragstart', function(e) {
            const timeMinutes = parseFloat(this.dataset.operationTime); // Получаем минуты
            const timeHours = timeMinutes / 60; // Конвертируем в часы

            const opData = {
                id: this.dataset.operationId,
                name: this.dataset.operationName,
                numberZnp: this.dataset.operationNumber,
                nomenclatureName: this.dataset.operationNomenclature,
                time: timeHours, // Сохраняем в часах
                durationMinutes: timeMinutes // Сохраняем оригинальное значение
            };
            console.log('opdata')
            console.log(opData)

            e.dataTransfer.setData('application/json', JSON.stringify(opData));
            this.classList.add('dragging');
        });
    });

    // Обработчики для области таймлайна
    timeSlots.addEventListener('dragover', function(e) {
        e.preventDefault();
        // Устанавливаем визуальный эффект
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
            if (!jsonData) {
                throw new Error('Нет данных операции');
            }

            const operationData = JSON.parse(jsonData);
            if (!operationData.time || !operationData.name) {
                throw new Error('Неполные данные операции');
            }

            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const pixelsPerMinute = 60 / zoomLevel;
            const minutes = Math.max(0, Math.round(x / pixelsPerMinute));

            scheduleOperation(operationData, minutes);
        } catch (error) {
            console.error('Ошибка при обработке операции:', error);
        }
    });
}

// Запланировать операцию
async function scheduleOperation(operationData, startMinutes) {
    try {
        // Проверяем, чтобы операция не выходила за пределы 24 часов
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

        console.log('New operation:', newOperation);

        // Добавляем операцию локально
        scheduledOperations.push(newOperation);
        saveOperationsToStorage();
        renderScheduledOperations();

        // Отправляем на сервер
        await addInTimeLine(operationData.id);

        console.log('Операция успешно запланирована и сохранена');
    } catch (error) {
        console.error('Ошибка при планировании операции:', error);

        // Откатываем изменения в случае ошибки
        if (operationData.id) {
            scheduledOperations = scheduledOperations.filter(op => op.id !== operationData.id);
            saveOperationsToStorage();
            renderScheduledOperations();
        }

        alert(error.message);
    }
}

async function addInTimeLine(id) {
    try {
        console.log('Adding to timeline, ID:', id);
        const response = await fetch(`/api/addInTimeLine/${id}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Server error: ${response.status} - ${errorText}`);
        }

        console.log('Операция успешно обновлена в базе данных');
        return true;
    } catch (error) {
        console.error('Ошибка при сохранении на сервере:', error);
        throw error; // Пробрасываем ошибку для обработки в scheduleOperation
    }
}

async function delFromTimeLine(id) {
    try {
        console.log('Удаление из timeline, ID:', id);

        const response = await fetch(`/api/delFromTimeLine/${id}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Ошибка сервера: ${response.status} - ${errorText}`);
        }

        // Если сервер не возвращает JSON, просто завершаем выполнение
        console.log('Операция успешно удалена из timeline');
        return true;
    } catch (error) {
        console.error('Ошибка при удалении из timeline:', error);
        throw error;
    }
}

// Обновление функции makeDraggable для работы с пикселями
function makeDraggable(element, index) {
    const SLOT_WIDTH = 20; // 10 минут = 20px
    let isDragging = false;
    let startX, startLeft;
    let moveHandler, upHandler;

    element.addEventListener('mousedown', function(e) {
        // Пропускаем клики по кнопке удаления
        if (e.target.classList.contains('delete-operation-btn')) {
            return;
        }

        isDragging = true;
        startX = e.clientX;
        startLeft = parseFloat(element.style.left);

        // Поднимаем элемент над другими
        element.style.zIndex = '1000';
        element.classList.add('dragging');

        // Создаем обработчики
        moveHandler = function(e) {
            if (!isDragging) return;

            const dx = e.clientX - startX;
            let newLeft = startLeft + dx;
            const width = parseFloat(element.style.width);

            newLeft = Math.max(0, Math.min(newLeft, 24 * 6 * SLOT_WIDTH - width));
            newLeft = Math.round(newLeft / SLOT_WIDTH) * SLOT_WIDTH;

            element.style.left = `${newLeft}px`;
        };

        upHandler = function() {
            if (!isDragging) return;

            document.removeEventListener('mousemove', moveHandler);
            document.removeEventListener('mouseup', upHandler);

            // Обновляем данные операции
            const newLeft = parseFloat(element.style.left);
            const startMinutes = (newLeft / SLOT_WIDTH) * 10;

            const op = scheduledOperations[index];
            const newStart = new Date(op.start);
            newStart.setHours(Math.floor(startMinutes / 60), startMinutes % 60);
            op.start = newStart.toISOString();

            // Возвращаем стили
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

// Обновление времени операции с учетом пикселей
function updateOperationTime(index, leftPx, widthPx) {
    const pixelsPerMinute = 60 / zoomLevel;
    const startMinutes = Math.round(leftPx / pixelsPerMinute);
    const durationMinutes = Math.round(widthPx / pixelsPerMinute);

    const op = scheduledOperations[index];
    const newStartDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        currentDate.getDate(),
        Math.floor(startMinutes / 60),
        startMinutes % 60
    );

    op.start = newStartDate.toISOString();
    op.end = new Date(newStartDate.getTime() + durationMinutes * 60000).toISOString();
    op.time = durationMinutes / 60;
    op.durationMinutes = durationMinutes;

    renderScheduledOperations();
    saveOperationsToStorage()
}

// Изменение масштаба
document.getElementById('zoom-slider').addEventListener('input', function(e) {
    zoomLevel = parseInt(e.target.value);
    document.getElementById('zoom-value').textContent = `${zoomLevel} мин`;
    renderTimeline();
});

// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', function() {
    initApp();
});

// Обработчик кликов по кнопкам удаления (должен быть после initDragOperations)
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('delete-btn')) {
        e.stopPropagation(); // Предотвращаем всплытие
        const opId = e.target.getAttribute('data-id');
        if (confirm('Удалить операцию?')) {
            // AJAX-запрос на удаление
            fetch(`/operations/${opId}`, { method: 'DELETE' })
                .then(response => {
                    if (response.ok) {
                        e.target.closest('.operation-card').remove();
                    }
                })
                .catch(console.error);
        }
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('operations-search');
    const clearSearchBtn = document.getElementById('clear-search');
    const operationCards = document.querySelectorAll('.operation-card');

    // Функция поиска
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

    // Обработчик ввода
    searchInput.addEventListener('input', filterOperations);

    // Очистка поиска
    clearSearchBtn.addEventListener('click', function() {
        searchInput.value = '';
        filterOperations();
    });
});

// Сохранение положения операции на таймлайне после добавления
function saveOperationsToStorage() {
    localStorage.setItem('scheduledOperations', JSON.stringify(scheduledOperations));
    localStorage.setItem('currentDate', currentDate.toISOString());
}

// Функция для загрузки данных при перезагрузке страницы
function loadOperationsFromStorage() {
    const savedOperations = localStorage.getItem('scheduledOperations');
    const savedDate = localStorage.getItem('currentDate');

    if (savedOperations) {
        scheduledOperations = JSON.parse(savedOperations).map(op => ({
            ...op,
            start: new Date(op.start), // Преобразуем строку обратно в Date
            end: new Date(op.end)
        }));
    }

    if (savedDate) {
        currentDate = new Date(savedDate);
    }
}

// Удаление операции с таймлайна
async function deleteOperation(index, id) {
    console.log('Deleting operation with index:', index, id);

    // Проверка валидности индекса
    if (index === undefined || index === null || isNaN(index)) {
        console.error('Invalid index:', index);
        return;
    }

    if (!confirm('Вы действительно хотите удалить эту операцию?')) {
        return;
    }

    const timeSlots = document.getElementById('time-slots');
    const operationEl = timeSlots?.querySelector(`.operation[data-index="${index}"]`);

    try {
        // 1. Визуальное удаление (если элемент существует)
        if (operationEl) {
            operationEl.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            operationEl.style.opacity = '0';
            operationEl.style.transform = 'scale(0.95)';
        }

        // 2. Сохраняем удаляемую операцию на случай отката
        const deletedOperation = scheduledOperations[index];

        // 3. Удаляем из локального хранилища
        scheduledOperations.splice(index, 1);
        saveOperationsToStorage();

        // 4. Вызываем delFromTimeLine и обрабатываем возможные ошибки
        await delFromTimeLine(id);

        // 5. Полное удаление элемента после успешного ответа сервера
        if (operationEl?.parentNode) {
            setTimeout(() => {
                operationEl.parentNode.removeChild(operationEl);
                renderScheduledOperations();
            }, 300);
        } else {
            renderScheduledOperations();
        }

    } catch (error) {
        console.error('Ошибка при удалении операции:', error);

        // Восстанавливаем операцию в случае ошибки
        scheduledOperations.splice(index, 0, deletedOperation);
        saveOperationsToStorage();

        // Восстанавливаем визуальное состояние
        if (operationEl) {
            operationEl.style.opacity = '1';
            operationEl.style.transform = 'scale(1)';
        }

        alert('Не удалось удалить операцию: ' + error.message);
    }
}