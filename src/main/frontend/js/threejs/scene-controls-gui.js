import * as THREE from 'three';

/**
 * Modul pro vytvoření GUI ovládání scény
 */

export function createSceneControlsGUI(controls, camera, renderFn, centerCameraFn) {
  const gui = document.createElement('div');
  gui.className = 'scene-controls-gui';
  gui.style.cssText = `
    position: absolute;
    bottom: 15px;
    right: 0;
    z-index: 1000;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
    background: rgba(0, 0, 0, 0.7);
    padding: 10px;
    border-radius: 6px 0 0 6px;
    user-select: none;
    transition: transform 0.3s ease;
    transform: translateX(0);
  `;

  let isVisible = true;

  const toggleButton = document.createElement('button');
  toggleButton.textContent = '►';
  toggleButton.className = 'scene-controls-toggle';
  toggleButton.style.cssText = `
    background: transparent;
    border: none;
    color: white;
    cursor: pointer;
    font-size: 12px;
    font-weight: bold;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.2s;
    padding: 0;
    flex-shrink: 0;
  `;

  toggleButton.addEventListener('mouseenter', () => {
    toggleButton.style.transform = 'scale(1.2)';
  });

  toggleButton.addEventListener('mouseleave', () => {
    toggleButton.style.transform = 'scale(1)';
  });

  toggleButton.addEventListener('click', () => {
    isVisible = !isVisible;
    if (isVisible) {
      gui.style.transform = 'translateX(0)';
      toggleButton.textContent = '►';
      toggleButton.title = 'Skrýt ovládání';
    } else {
      const offset = gui.offsetWidth - 40;
      gui.style.transform = `translateX(${offset}px)`;
      toggleButton.textContent = '◄';
      toggleButton.title = 'Zobrazit ovládání';
    }
  });

  toggleButton.title = 'Skrýt ovládání';

  gui.appendChild(toggleButton);

  const rotateSpeed = 0.1;
  const moveSpeed = 0.5;

  let intervalId = null;

  const createButton = (text, gridArea, action, isResetButton = false) => {
    const button = document.createElement('button');
    button.textContent = text;
    button.style.cssText = `
      grid-area: ${gridArea};
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      border-radius: 3px;
      cursor: pointer;
      font-size: ${isResetButton ? '16px' : '14px'};
      font-weight: bold;
      transition: background 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
    `;

    button.addEventListener('mouseenter', () => {
      button.style.background = 'rgba(255, 255, 255, 0.3)';
    });

    button.addEventListener('mouseleave', () => {
      button.style.background = 'rgba(255, 255, 255, 0.2)';
    });

    if (isResetButton) {
      button.addEventListener('click', () => {
        action();
        renderFn();
      });
    } else {
      button.addEventListener('mousedown', () => {
        button.style.background = 'rgba(255, 255, 255, 0.4)';
        action();
        renderFn();
        intervalId = setInterval(() => {
          action();
          renderFn();
        }, 50);
      });

      const stopAction = () => {
        button.style.background = 'rgba(255, 255, 255, 0.3)';
        if (intervalId) {
          clearInterval(intervalId);
          intervalId = null;
        }
      };

      button.addEventListener('mouseup', stopAction);
      button.addEventListener('mouseleave', stopAction);
    }

    return button;
  };

  const controlsContainer = document.createElement('div');
  controlsContainer.style.cssText = `
    display: grid;
    grid-template-columns: repeat(3, 35px);
    grid-template-rows: repeat(3, 35px);
    gap: 4px;
  `;

  const upButton = createButton('▲', '1 / 2 / 2 / 3', () => {
    const spherical = new THREE.Spherical();
    const offset = new THREE.Vector3();
    offset.copy(camera.position).sub(controls.target);
    spherical.setFromVector3(offset);
    spherical.phi = Math.max(0.1, Math.min(Math.PI - 0.1, spherical.phi - rotateSpeed));
    offset.setFromSpherical(spherical);
    camera.position.copy(controls.target).add(offset);
    controls.update();
  });

  const downButton = createButton('▼', '3 / 2 / 4 / 3', () => {
    const spherical = new THREE.Spherical();
    const offset = new THREE.Vector3();
    offset.copy(camera.position).sub(controls.target);
    spherical.setFromVector3(offset);
    spherical.phi = Math.max(0.1, Math.min(Math.PI - 0.1, spherical.phi + rotateSpeed));
    offset.setFromSpherical(spherical);
    camera.position.copy(controls.target).add(offset);
    controls.update();
  });

  const leftButton = createButton('◄', '2 / 1 / 3 / 2', () => {
    const spherical = new THREE.Spherical();
    const offset = new THREE.Vector3();
    offset.copy(camera.position).sub(controls.target);
    spherical.setFromVector3(offset);
    spherical.theta -= rotateSpeed;
    offset.setFromSpherical(spherical);
    camera.position.copy(controls.target).add(offset);
    controls.update();
  });

  const rightButton = createButton('►', '2 / 3 / 3 / 4', () => {
    const spherical = new THREE.Spherical();
    const offset = new THREE.Vector3();
    offset.copy(camera.position).sub(controls.target);
    spherical.setFromVector3(offset);
    spherical.theta += rotateSpeed;
    offset.setFromSpherical(spherical);
    camera.position.copy(controls.target).add(offset);
    controls.update();
  });

  const resetButton = createButton('⟲', '2 / 2 / 3 / 3', () => {
    if (centerCameraFn) {
      centerCameraFn();
    } else {
      controls.reset();
      controls.update();
    }
  }, true);
  resetButton.title = 'Vycentrovat kameru na model';

  controlsContainer.appendChild(upButton);
  controlsContainer.appendChild(downButton);
  controlsContainer.appendChild(leftButton);
  controlsContainer.appendChild(rightButton);
  controlsContainer.appendChild(resetButton);

  gui.appendChild(controlsContainer);

  const zoomContainer = document.createElement('div');
  zoomContainer.style.cssText = `
    display: flex;
    flex-direction: column;
    gap: 4px;
  `;

  const zoomInButton = createButton('+', 'auto', () => {
    const direction = new THREE.Vector3();
    direction.subVectors(controls.target, camera.position).normalize();
    camera.position.add(direction.multiplyScalar(moveSpeed));
    controls.update();
  });
  zoomInButton.style.width = '35px';
  zoomInButton.style.height = '53px';
  zoomInButton.style.fontSize = '16px';

  const zoomOutButton = createButton('−', 'auto', () => {
    const direction = new THREE.Vector3();
    direction.subVectors(controls.target, camera.position).normalize();
    camera.position.sub(direction.multiplyScalar(moveSpeed));
    controls.update();
  });
  zoomOutButton.style.width = '35px';
  zoomOutButton.style.height = '53px';
  zoomOutButton.style.fontSize = '16px';

  zoomContainer.appendChild(zoomInButton);
  zoomContainer.appendChild(zoomOutButton);

  gui.appendChild(zoomContainer);

  return gui;
}

/**
 * Připojí GUI do canvasu
 */
export function attachGUIToCanvas(canvasElement, gui) {
  const parent = canvasElement.parentElement;
  if (parent) {
    const parentStyle = window.getComputedStyle(parent);
    if (parentStyle.position === 'static') {
      parent.style.position = 'relative';
    }
    parent.appendChild(gui);
  }
}

/**
 * Odstraní GUI ze scény
 */
export function removeGUI(gui) {
  if (gui && gui.parentElement) {
    gui.parentElement.removeChild(gui);
  }
}

