import { getCurrentResourceId, setCurrentResourceId } from "./StateManager.js";
import {timelineScrollWidth} from "./ScrollScript.js";

export function initResourceTabs() {
    const tabs = document.querySelectorAll('.resource-tab');
    const timelines = document.querySelectorAll('.resource-timeline');

    if (tabs.length === 0 || timelines.length === 0) {
        console.error('Resource tabs or timelines not found in DOM');
        return;
    }

    // Активируем сохраненную или первую вкладку
    const savedResourceId = getCurrentResourceId();
    const validResourceId = Array.from(tabs).some(tab => tab.dataset.resourceId === savedResourceId)
        ? savedResourceId
        : tabs[0].dataset.resourceId;

    setCurrentResourceId(validResourceId);
    activateTabAndTimeline(validResourceId);

    // Назначаем обработчики кликов
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const resourceId = tab.dataset.resourceId;
            if (resourceId !== getCurrentResourceId()) {
                setCurrentResourceId(resourceId);
                activateTabAndTimeline(resourceId);
            }
        });
    });
}

function activateTabAndTimeline(resourceId) {
    // Деактивируем все
    document.querySelectorAll('.resource-tab.active, .resource-timeline.active')
        .forEach(el => el.classList.remove('active'));

    timelineScrollWidth()
    // Активируем нужные
    document.querySelector(`.resource-tab[data-resource-id="${resourceId}"]`)?.classList.add('active');
    document.querySelector(`.resource-timeline[data-resource-id="${resourceId}"]`)?.classList.add('active');
}