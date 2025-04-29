// Скролл полосы таймлайна -->
import {getZoomLevel, renderTimeline, setZoomLevel} from "./MainScript.js";

export function timelineScrollWidth() {
    const MAX_ATTEMPTS = 2;
    let attempts = 0;

    const scrollToCenter = () => {
        // Находим АКТИВНУЮ временную шкалу (с классом .active)
        const activeTimeline = document.querySelector('.resource-timeline.active');
        if (!activeTimeline) {
            return false;
        }

        const timelineContainer = activeTimeline.querySelector('.timeline-container');
        if (!timelineContainer) {
            console.error('Timeline container not found in active timeline');
            return false;
        }

        if (timelineContainer.scrollWidth <= timelineContainer.clientWidth) {
            return true;
        }

        const centerPosition = (timelineContainer.scrollWidth / 2 - timelineContainer.clientWidth / 2) + 175;


        if (centerPosition > 0 && !isNaN(centerPosition)) {
            timelineContainer.scrollLeft = centerPosition;
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
    }, 300);
}

export function setupZoomControls() {
    const zoomSlider = document.getElementById('zoom-slider');
    const zoomValue = document.getElementById('zoom-value');

    // Настройки слайдера (5, 10, 15, 20 минут)
    zoomSlider.min = 5;
    zoomSlider.max = 20;
    zoomSlider.step = 5;
    zoomSlider.value = getZoomLevel();
    zoomValue.textContent = `${getZoomLevel()} мин`;

    zoomSlider.addEventListener('input', function(e) {
        setZoomLevel(parseInt(e.target.value));
        zoomValue.textContent = `${getZoomLevel()} мин`;
        renderTimeline();
    });
}