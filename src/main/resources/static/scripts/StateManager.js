let _currentResourceId = localStorage.getItem('currentResourceId');
let _renderCallback = null;

export function getCurrentResourceId() {
    return _currentResourceId;
}

export function setCurrentResourceId(id, callback = null) {
    _currentResourceId = id;
    localStorage.setItem('currentResourceId', id);

    if (callback) {
        _renderCallback = callback;
    }

    if (_renderCallback) {
        _renderCallback();
    }
}

export function initRenderCallback(callback) {
    _renderCallback = callback;
}