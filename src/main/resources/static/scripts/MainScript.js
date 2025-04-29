import {initMonthNavigation, renderCalendar, updateDateDisplay} from './CalendarScript.js';
import {setupZoomControls, timelineScrollWidth} from "./ScrollScript.js";
import {initOperationSplitting} from "./OperationSplitScript.js";
import {filterOperations, setupSearch} from "./FilterScript.js";
import {addManualOperationToTimeline} from "./ManualOperationScript.js";
import {initResourceTabs} from "./ResourceTabScript.js";
import {getCurrentResourceId, initRenderCallback } from "./StateManager.js";

// MainScript.js
let _currentDate = new Date();
_currentDate.setHours(0, 0, 0, 0); // Обнуляем время при инициализации

export function getCurrentDate() {
    return new Date(_currentDate); // Возвращаем копию
}

export function setCurrentDate(date) {
    _currentDate = new Date(date);
    _currentDate.setHours(0, 0, 0, 0); // Обнуляем время
    console.log('Date changed to:', _currentDate);
}

export let zoomLevel = 10; // минут на ячейку

export function getZoomLevel() {
    return zoomLevel; // Возвращаем копию
}

export function setZoomLevel(level) {
    zoomLevel = level;
}

export let scheduledOperations = {}; // Объект для хранения операций по ресурсам
// Глобальные переменные для хранения выбранной операции
let selectedOperationId = null;
let selectedOperationElement = null;

// Инициализация приложения
async function initApp() {
    // 1. Загрузка данных
    const today = new Date();
    setCurrentDate(new Date(today.getFullYear(), today.getMonth(), today.getDate()));

    const loadedData = await loadOperationsFromBackend();
    if (loadedData) {
        scheduledOperations = loadedData.operations;
    }

    // 2. Инициализация UI
    renderCalendar();
    updateDateDisplay();
    initMonthNavigation();

    // 3. Инициализация таймлайна
    initRenderCallback(renderTimeline);
    initResourceTabs();

    // 4. Остальная инициализация
    initDragOperations();
    initContextMenu();
    setupZoomControls();
    setupOperationForm();
    await updateAvailableOperations();
    timelineScrollWidth();

    // 5. Создаем контейнер для подсказок
    const tooltipContainer = document.createElement('div');
    tooltipContainer.className = 'operation-tooltip';
    document.body.appendChild(tooltipContainer);

    // 6. Первичный рендеринг
    renderTimeline();

}

/* Таймлайн */

export function renderTimeline() {
    const resourceId = getCurrentResourceId();
    if (!resourceId) {
        console.error('No resource ID available');
        return;
    }

    const timeline = document.querySelector(`.resource-timeline[data-resource-id="${resourceId}"]`);
    if (!timeline) {
        console.error(`Timeline not found for resource ${resourceId}`);
        return;
    }

    const cellWidth = 20 * (10 / zoomLevel);

    renderTimeRuler(timeline, cellWidth);
    renderTimeSlots(timeline, cellWidth);
    renderScheduledOperations(timeline, cellWidth);
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


function renderScheduledOperations(timelineContainer, cellWidth) {
    const timeSlots = timelineContainer.querySelector('.time-slots');
    if (!timeSlots) return;

    // Удаляем старые операции
    timeSlots.querySelectorAll('.operation').forEach(op => op.remove());

    const operationsForResource = scheduledOperations[getCurrentResourceId()] || [];

    const tooltipContainer = document.querySelector('.operation-tooltip');

    operationsForResource.forEach((op, index) => {
        if (!op || !op.start) return;

        const opStart = new Date(op.start);
        if (opStart.toDateString() !== getCurrentDate().toDateString()) return;

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
        operationEl.dataset.resourceId = getCurrentResourceId();
        operationEl.dataset.operationId = op.id;

        // Форматирование данных для подсказки
        const formattedDuration = parseFloat(formatDuration(durationMinutes)).toFixed(2);
        const startTime = `${opStart.getHours().toString().padStart(2, '0')}:${opStart.getMinutes().toString().padStart(2, '0')}`;
        const endTime = calculateEndTime(startTime, formattedDuration);

        operationEl.dataset.tooltip = `
            ID: ${op.id}
            Номер ЗНП: ${op.number || 'Без номера'}
            Название: ${op.name || 'Без названия'}
            Время: ${formattedDuration}
            Начало: ${startTime}
            Конец: ${endTime}
            Номенклатура: ${op.nomenclatureName || 'Без названия'}
            
        `.trim();

        // Создаем элементы операции
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'delete-operation-btn';
        deleteBtn.innerHTML = '×';
        deleteBtn.dataset.index = index;
        deleteBtn.dataset.resourceId = getCurrentResourceId();
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

function calculateEndTime(startTime, durationStr) {
    // Разбиваем время начала на часы и минуты
    const [startHours, startMinutes] = startTime.split(':').map(Number);

    // Разбиваем длительность на минуты и секунды
    const durationParts = durationStr.split('.');
    const durationMinutes = parseInt(durationParts[0]) || 0;
    const durationSeconds = parseInt(durationParts[1]) || 0;

    // Создаём объект Date (используем текущую дату)
    const date = new Date();
    date.setHours(startHours, startMinutes, 0, 0);

    // Добавляем минуты и секунды
    date.setMinutes(date.getMinutes() + durationMinutes);
    date.setSeconds(date.getSeconds() + durationSeconds);

    // Форматируем результат в "HH:mm:ss"
    const endHours = String(date.getHours()).padStart(2, '0');
    const endMinutes = String(date.getMinutes()).padStart(2, '0');
    const endSeconds = String(date.getSeconds()).padStart(2, '0');

    return `${endHours}:${endMinutes}:${endSeconds}`;
}

export function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours > 0 ? hours + 'ч ' : ''}${mins > 0 ? mins + 'м' : ''}`.trim();
}

/* <-- Таймлайн */


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
    let originalLeft;

    element.addEventListener('mousedown', function(e) {
        if (e.target.classList.contains('delete-operation-btn')) return;

        isDragging = true;
        startX = e.clientX;
        startLeft = parseFloat(element.style.left);
        originalLeft = startLeft; // Сохраняем оригинальную позицию
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

            // Проверка коллизий с другими операциями
            const resourceId = element.dataset.resourceId;
            const operations = scheduledOperations[resourceId] || [];
            const currentWidth = parseFloat(element.style.width);

            // Находим ближайшие операции слева и справа
            let leftBoundary = 0;
            let rightBoundary = 24 * 6 * cellWidth - currentWidth;

            operations.forEach((op, i) => {
                if (i === index) return;

                const opLeft = (new Date(op.start).getHours() * 60 + new Date(op.start).getMinutes()) / 10 * cellWidth;
                const opWidth = (op.durationMinutes / 10) * cellWidth;

                // Проверяем операции слева
                if (opLeft + opWidth <= originalLeft && opLeft + opWidth > leftBoundary) {
                    leftBoundary = opLeft + opWidth;
                }

                // Проверяем операции справа
                if (opLeft >= originalLeft + currentWidth && opLeft < rightBoundary) {
                    rightBoundary = opLeft;
                }
            });

            // Применяем границы
            if (newLeft < leftBoundary) {
                newLeft = leftBoundary;
            } else if (newLeft + currentWidth > rightBoundary) {
                newLeft = rightBoundary - currentWidth;
            }

            element.style.left = `${newLeft}px`;
        };

        const upHandler = function() {
            if (!isDragging) return;
            document.removeEventListener('mousemove', moveHandler);
            document.removeEventListener('mouseup', upHandler);

            const newLeft = parseFloat(element.style.left);
            const startMinutes = (newLeft / cellWidth) * 10; // 10 - базовый zoom

            // Обновляем время операции только если позиция изменилась
            if (Math.abs(newLeft - originalLeft) >= cellWidth) {
                const resourceId = element.dataset.resourceId;
                if (scheduledOperations[resourceId] && scheduledOperations[resourceId][index]) {
                    const op = scheduledOperations[resourceId][index];
                    const newStart = new Date(op.start);
                    newStart.setHours(Math.floor(startMinutes / 60), startMinutes % 60);
                    op.start = newStart.toISOString();

                    // Пересчитываем время окончания
                    const endDate = new Date(newStart.getTime() + op.durationMinutes * 60000);
                    op.end = endDate.toISOString();

                    saveOperationsToBackend();
                }
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
            getCurrentDate().getFullYear(),
            getCurrentDate().getMonth(),
            getCurrentDate().getDate(),
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
        if (!scheduledOperations[getCurrentResourceId()]) {
            scheduledOperations[getCurrentResourceId()] = []; // Вот ключевое исправление
        }

        // Проверяем, что scheduledOperations[currentResourceId] является массивом
        if (!Array.isArray(scheduledOperations[getCurrentResourceId()])) {
            scheduledOperations[getCurrentResourceId()] = [];
        }

        scheduledOperations[getCurrentResourceId()].push(newOperation);
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


/* Сохранение и загрузка операций таймлайна в бд --> */
export async function saveOperationsToBackend() {
    const data = {
        operations: JSON.stringify(scheduledOperations)
    };

    try {
        const response = await fetch('api/save-operations', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        // Проверяем, есть ли тело ответа
        const text = await response.text();
        if (!text) {
            return { status: response.status, message: 'Empty response' };
        }

        try {
            return JSON.parse(text);
        } catch (e) {
            console.warn('Response is not JSON:', text);
            return {
                status: response.status,
                data: text,
                error: 'Response was not valid JSON'
            };
        }
    } catch (error) {
        console.error('Save failed:', error);
        return {
            error: error.message,
            stack: error.stack
        };
    }
}

async function loadOperationsFromBackend() {
    const url = 'api/load-operations';

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);

        const data = await response.json(); // Получаем весь ответ сервера

        // Если сервер возвращает массив операций (например, история)
        if (Array.isArray(data)) {
            if (!data.length) {
                console.warn('No operations received!');
                return null;
            }

            const latest = data[data.length - 1];
            const operations = typeof latest.operations === 'string'
                ? JSON.parse(latest.operations)
                : latest.operations;

            const parsedData = {
                operations, // Уже распаршенный объект
                date: latest.operationDate ? new Date(latest.operationDate) : null,
                resourceId: latest.resourceId
            };

            return parsedData;
        }
        // Если сервер возвращает сразу объект operations (без массива)
        else if (data.operations) {
            const operations = typeof data.operations === 'string'
                ? JSON.parse(data.operations)
                : data.operations;

            const parsedData = {
                operations,
                date: data.operationDate ? new Date(data.operationDate) : null,
                resourceId: data.resourceId
            };


            return parsedData;
        }
        // Если структура ответа неизвестна
        else {
            console.error('Unexpected server response format:', data);
            return null;
        }
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

    const operationEl = document.querySelector(`.operation[data-index="${index}"][data-resource-id="${getCurrentResourceId()}"]`);
    let deletedOperation = null;

    try {
        if (operationEl) {
            operationEl.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            operationEl.style.opacity = '0';
            operationEl.style.transform = 'scale(0.95)';
        }

        if (scheduledOperations[getCurrentResourceId()] && scheduledOperations[getCurrentResourceId()][index]) {
            deletedOperation = scheduledOperations[getCurrentResourceId()][index];
            scheduledOperations[getCurrentResourceId()].splice(index, 1);

            await saveOperationsToBackend();
        }

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

        if (deletedOperation && scheduledOperations[getCurrentResourceId()]) {
            scheduledOperations[getCurrentResourceId()].splice(index, 0, deletedOperation);

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



export async function updateAvailableOperations() {
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

        const opId = operationElement.dataset.operationId;

        e.preventDefault();


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
                resourceId: getCurrentResourceId()
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
    const loadedData = await loadOperationsFromBackend();
    scheduledOperations = loadedData.operations;
}
// <-- Меню удаления с таймлайна

// Фильтры по time-info-tabs -->
function setFilter() {
    const infoTabs = document.querySelectorAll('.time-info-tabs');
    const searchInput = document.getElementById('operations-search');

    infoTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const nameElement = this.querySelector('.time-info-name');
            if (nameElement && searchInput) {
                // 1. Заполняем поле поиска
                searchInput.value = nameElement.textContent.trim();

                // 2. Вручную запускаем поиск
                filterOperations();

                // 3. Альтернативно: триггерим событие input
                // searchInput.dispatchEvent(new Event('input'));
            }
        });
    });

    setupSearch();
}
// <-- Фильтры по time-info-tabs

// Вызываем при загрузке и после рендеринга таймлайна
document.addEventListener('DOMContentLoaded', timelineScrollWidth);
window.addEventListener('load', timelineScrollWidth);
document.addEventListener('DOMContentLoaded', setFilter);

// <-- Скролл полосы таймлайна
// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', initApp);