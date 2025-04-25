/* Календарь */
import { currentDate } from './MainScript.js';
import { renderTimeline } from './MainScript.js';

export function initMonthNavigation() {
    document.getElementById('next-month').addEventListener('click', handleNextMonth);
    document.getElementById('prev-month').addEventListener('click', handlePrevMonth);
}

export function handleNextMonth() {
    changeMonth(1);
}

export function handlePrevMonth() {
    changeMonth(-1);
}

export function changeMonth(offset) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + offset,
        1
    );
    renderCalendar();
}

export function renderCalendar() {
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

    // Добавляем дни предыдущего месяца
    for (let i = 1; i < firstDayOfWeek; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        calendarDays.appendChild(dayElement);
    }

    // Добавляем дни текущего месяца
    const today = new Date();
    for (let i = 1; i <= daysInMonth; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day';
        dayElement.textContent = i;

        // Правильная проверка текущего дня
        if (i === today.getDate() &&
            currentDate.getMonth() === today.getMonth() &&
            currentDate.getFullYear() === today.getFullYear()) {
            dayElement.classList.add('current');
        }

        dayElement.addEventListener('click', () => selectDate(i));
        calendarDays.appendChild(dayElement);
    }

    // Добавляем дни следующего месяца
    const totalCells = Math.ceil((firstDayOfWeek - 1 + daysInMonth) / 7) * 7;
    const remainingCells = totalCells - (firstDayOfWeek - 1 + daysInMonth);

    for (let i = 1; i <= remainingCells; i++) {
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day other-month';
        calendarDays.appendChild(dayElement);
    }
}

export function selectDate(day) {
    currentDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        day
    );
    updateDateDisplay();
    renderTimeline();
}

export function updateDateDisplay() {
    const daysOfWeek = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"];

    // Форматируем дату для отображения
    const formattedDate = `${daysOfWeek[currentDate.getDay()]}, ${currentDate.getDate()} ${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;
    document.getElementById('current-date').textContent = formattedDate;
}