// Функция добавления ручной операции в таймлайн
import {renderTimeline, saveOperationsToBackend, scheduledOperations} from "./MainScript.js";
import { getCurrentResourceId} from "./StateManager.js";

let currentResourceId = getCurrentResourceId();


export function addManualOperationToTimeline() {
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
    const random = Math.floor(Math.random() * 10000);

    // Создаем объект операции с оранжевым цветом
    const operationData = {
        id: 'manual' + random,
        name: name,
        number: 'Ручная операция',
        nomenclatureName: 'Ручное добавление',
        durationMinutes: duration,
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

    currentResourceId = getCurrentResourceId();

    // Сохраняем в scheduledOperations
    if (!scheduledOperations[currentResourceId]) {
        scheduledOperations[currentResourceId] = [];
    }
    scheduledOperations[currentResourceId].push(newOperation);
    console.log('Save manual operation')

    await saveOperationsToBackend();
    renderTimeline();
}