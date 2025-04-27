import { getCurrentDate, setCurrentDate, renderTimeline } from './MainScript.js';

// Инициализация навигации по месяцам
export function initMonthNavigation() {
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
    const date = getCurrentDate();
    setCurrentDate(new Date(
        date.getFullYear(),
        date.getMonth() + offset,
        1
    ));
    renderCalendar();
}

// Рендеринг календаря
export function renderCalendar() {
    const currentDate = getCurrentDate();
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

    // Дни предыдущего месяца
    for (let i = 1; i < firstDayOfWeek; i++) {
        calendarDays.appendChild(createDayElement('other-month'));
    }

    // Дни текущего месяца
    const today = new Date();
    const selectedDate = getCurrentDate(); // Получаем текущую выбранную дату

    for (let i = 1; i <= daysInMonth; i++) {
        const dayElement = createDayElement('calendar-day', i.toString());

        // Подсветка сегодняшнего дня
        if (i === today.getDate() &&
            currentDate.getMonth() === today.getMonth() &&
            currentDate.getFullYear() === today.getFullYear()) {
            dayElement.classList.add('current');
        }

        // Подсветка выбранного дня
        if (i === selectedDate.getDate() &&
            currentDate.getMonth() === selectedDate.getMonth() &&
            currentDate.getFullYear() === selectedDate.getFullYear()) {
            dayElement.classList.add('selected');
        }

        dayElement.addEventListener('click', () => selectDate(i));
        calendarDays.appendChild(dayElement);
    }

    // Дни следующего месяца
    const totalCells = Math.ceil((firstDayOfWeek - 1 + daysInMonth) / 7) * 7;
    const remainingCells = totalCells - (firstDayOfWeek - 1 + daysInMonth);

    for (let i = 1; i <= remainingCells; i++) {
        calendarDays.appendChild(createDayElement('other-month'));
    }
}

function createDayElement(className, text = '') {
    const dayElement = document.createElement('div');
    dayElement.className = className;
    if (text) dayElement.textContent = text;
    return dayElement;
}

// Выбор даты
export function selectDate(day) {
    const date = getCurrentDate();
    const newDate = new Date(
        date.getFullYear(),
        date.getMonth(),
        day
    );
    setCurrentDate(newDate);
    updateDateDisplay();
    renderTimeline(); // Теперь рендерим после обновления даты
    renderCalendar(); // Перерисовываем календарь для подсветки выбранного дня
}

// Обновление отображения даты
export function updateDateDisplay() {
    const currentDate = getCurrentDate();
    const daysOfWeek = ["Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"];
    const monthNames = ["января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"];

    document.getElementById('current-date').textContent =
        `${daysOfWeek[currentDate.getDay()]}, ${currentDate.getDate()} ${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;
}