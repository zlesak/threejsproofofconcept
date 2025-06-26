window.scrollToDataId = function(dataId) {
    const element = document.querySelector(`[data-id="${dataId}"]`);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } else {
        console.warn(`Element with data-id="${dataId}" not found.`);
    }
};