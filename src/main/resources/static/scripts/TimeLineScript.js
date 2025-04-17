// TimeLineScript.js
let currentDate = new Date();
currentDate.setHours(0, 0, 0, 0);
let zoomLevel = 30; // минут на ячейку
let scheduledOperations = [];

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
    const timeSlots = document.getElementById('time-slots');
    timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

    const HOUR_WIDTH = 120; // Фиксированная ширина часа (120px)

    scheduledOperations.forEach((op, index) => {
        const opStart = new Date(op.start);
        if (opStart.toDateString() !== currentDate.toDateString()) return;

        const startMinutes = opStart.getHours() * 60 + opStart.getMinutes();
        const durationMinutes = op.durationMinutes || (op.time * 60);

        // Позиция и ширина в пикселях
        const left = (startMinutes / 10) * 20; // 10 минут = 20px
        const width = (durationMinutes / 10) * 20;

        const operationEl = document.createElement('div');
        operationEl.className = 'operation';
        operationEl.style.left = `${left}px`;
        operationEl.style.width = `${width}px`;
        operationEl.textContent = `${op.name} (${formatDuration(durationMinutes)})`;
        operationEl.dataset.index = index;

        // Добавляем атрибут для всплывающей подсказки
        operationEl.setAttribute('data-tooltip',
            `Номер ЗНП: ${op.number || 'не указан'}\n` +
            `Название: ${op.name}\n` +
            `Время: ${(op.time * 60).toFixed(1)} мин\n` +
            `Номенклатура: ${op.nomenclatureName || 'не указана'}`);

        timeSlots.appendChild(operationEl);
        makeDraggable(operationEl, index);
    });
}

// Вспомогательная функция для форматирования длительности
function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    const formattedMins = mins > 0 ? mins.toFixed(2) + 'м' : '';

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
    const SLOT_WIDTH = 20; // 10 минут = 20px
    const MINUTES_PER_SLOT = 10;

    element.addEventListener('mousedown', function(e) {
        e.preventDefault();

        const startX = e.clientX;
        const startLeft = parseFloat(element.style.left);
        const width = parseFloat(element.style.width);
        const slotsCount = Math.round(width / SLOT_WIDTH);

        function moveHandler(e) {
            const dx = e.clientX - startX;
            let newLeft = startLeft + dx;

            // Ограничиваем перемещение и выравниваем по слотам
            newLeft = Math.max(0, Math.min(newLeft, 24 * 6 * SLOT_WIDTH - width));
            newLeft = Math.round(newLeft / SLOT_WIDTH) * SLOT_WIDTH;

            element.style.left = `${newLeft}px`;
        }

        function upHandler() {
            document.removeEventListener('mousemove', moveHandler);
            document.removeEventListener('mouseup', upHandler);

            // Обновляем данные операции
            const newLeft = parseFloat(element.style.left);
            const startMinutes = (newLeft / SLOT_WIDTH) * MINUTES_PER_SLOT;

            const newStart = new Date(scheduledOperations[index].start);
            newStart.setHours(Math.floor(startMinutes / 60), startMinutes % 60);
            scheduledOperations[index].start = newStart;

            renderScheduledOperations();
        }

        document.addEventListener('mousemove', moveHandler);
        document.addEventListener('mouseup', upHandler);
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