// Editorjs - utiklity functions

export function attachTextureColorListeners() {
  const editorContainer = document.getElementById('editorjs');
  if (!editorContainer) return;
  const links = editorContainer.querySelectorAll('a[data-model-id][data-texture-id][data-hex-color]');
  links.forEach(link => {
    if (!link.hasAttribute('data-texture-color-listener')) {
      link.addEventListener('click', (event) => {
        event.preventDefault();
        let customElement = link.closest('editor-js');
        if (customElement) {
          customElement.dispatchEvent(new CustomEvent('texturecolorareaclick', {
            bubbles: true,
            composed: true,
            detail: {
              modelId: link.getAttribute('data-model-id'),
              textureId: link.getAttribute('data-texture-id'),
              hexColor: link.getAttribute('data-hex-color'),
              text: link.textContent
            }
          }));
        }
      });
      link.setAttribute('data-texture-color-listener', 'true');
    }
  });
}
