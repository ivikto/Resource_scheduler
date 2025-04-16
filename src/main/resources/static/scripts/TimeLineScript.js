// Текущая дата и настройки
let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);

let zoomLevel = 30; // минут на ячейку
let scheduledOperations = []; // Только запланированные операции

// Инициализация приложения
function initApp() {
    renderCalendar();
    updateDateDisplay();
    renderTimeline();
    initDragOperations();
}

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

// Переключение месяцев (остается без изменений)
function changeMonth(offset) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + offset,
        1
    );
    renderCalendar();
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

    // Рассчитываем ширину часа в пикселях
    const hourWidth = (60 / zoomLevel) * 60 * 2; // 2 часа на метку

    for (let hour = 0; hour < 24; hour += 2) {
        const hourElement = document.createElement('div');
        hourElement.className = 'time-ruler-hour';
        hourElement.style.width = `${hourWidth}px`;
        hourElement.style.left = `${hour * (60 / zoomLevel) * 60}px`;

        const hourLabel = document.createElement('div');
        hourLabel.className = 'time-ruler-hour-label';
        hourLabel.textContent = `${hour}:00 - ${hour+2}:00`;

        hourElement.appendChild(hourLabel);
        timeRuler.appendChild(hourElement);
    }
}

// Отрисовка временных слотов с учетом масштаба
function renderTimeSlots() {
    const timeSlots = document.getElementById('time-slots');
    timeSlots.innerHTML = '';

    // Ширина слота в пикселях
    const slotWidth = 60; // 30 минут при zoomLevel=30

    // Создаем слоты для всех 24 часов
    for (let i = 0; i < 24 * 60; i += zoomLevel) {
        const slot = document.createElement('div');
        slot.className = 'time-slot';
        slot.style.left = `${i * (60 / zoomLevel)}px`;
        slot.style.width = `${slotWidth}px`;
        timeSlots.appendChild(slot);
    }
}

// Отрисовка запланированных операций с правильным позиционированием
function renderScheduledOperations() {
    const timeSlots = document.getElementById('time-slots');
    timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

    scheduledOperations.forEach((op, index) => {
        const opStart = new Date(op.start);
        if (opStart.toDateString() !== currentDate.toDateString()) return;

        const startMinutes = opStart.getHours() * 60 + opStart.getMinutes();
        const durationMinutes = op.durationMinutes || (op.time * 60);

        // Позиция и ширина в пикселях
        const left = startMinutes * (60 / zoomLevel);
        const width = durationMinutes * (60 / zoomLevel);

        const operationEl = document.createElement('div');
        operationEl.className = 'operation';
        operationEl.style.left = `${left}px`;
        operationEl.style.width = `${width}px`;
        operationEl.textContent = `${op.name} (${(durationMinutes/60).toFixed(1)} ч)`;
        operationEl.dataset.index = index;

        const dragHandle = document.createElement('div');
        dragHandle.className = 'drag-handle';
        operationEl.appendChild(dragHandle);

        timeSlots.appendChild(operationEl);
        makeDraggable(operationEl, index);
    });
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
                time: timeHours, // Сохраняем в часах
                durationMinutes: timeMinutes // Сохраняем оригинальное значение
            };

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
function scheduleOperation(operationData, startMinutes) {
    // Проверяем, чтобы операция не выходила за пределы 24 часов
    const durationMinutes = operationData.durationMinutes || (operationData.time * 60);
    const endMinutes = startMinutes + durationMinutes;

    if (endMinutes > 24 * 60) {
        alert('Операция не может выходить за пределы 24 часов');
        return;
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
        time: durationMinutes / 60,
        durationMinutes: durationMinutes,
        start: startDate.toISOString(),
        end: new Date(startDate.getTime() + durationMinutes * 60000).toISOString()
    };

    scheduledOperations.push(newOperation);
    renderScheduledOperations();
}

// Обновление функции makeDraggable для работы с пикселями
function makeDraggable(element, index) {
    let isDragging = false;
    let isResizing = false;
    let startX, startLeft, startWidth;

    element.addEventListener('mousedown', function(e) {
        if (e.target.classList.contains('drag-handle')) {
            isResizing = true;
            startX = e.clientX;
            startWidth = parseFloat(element.style.width);
        } else {
            isDragging = true;
            startX = e.clientX;
            startLeft = parseFloat(element.style.left);
        }
        e.preventDefault();
    });

    document.addEventListener('mousemove', function(e) {
        if (!isDragging && !isResizing) return;

        const timeSlots = document.getElementById('time-slots');
        const rect = timeSlots.getBoundingClientRect();
        const x = e.clientX - rect.left;

        if (isDragging) {
            const newLeft = Math.max(0, Math.min(x, 24*60*(60/zoomLevel) - element.offsetWidth));
            element.style.left = `${newLeft}px`;
        } else if (isResizing) {
            const newWidth = Math.max(30, x - parseFloat(element.style.left));
            element.style.width = `${newWidth}px`;
        }
    });

    document.addEventListener('mouseup', function(e) {
        if (isDragging || isResizing) {
            updateOperationTime(
                index,
                parseFloat(element.style.left),
                parseFloat(element.style.width)
            );
        }
        isDragging = false;
        isResizing = false;
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