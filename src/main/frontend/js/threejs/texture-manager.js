import * as THREE from 'three';

/**
 * Přidá další textury k modelu
 */
export async function addOtherTextures(textureMap, modelId, models) {
  const modelObject = models.find(m => m.id === modelId);

  if (!modelObject) {
    console.error('addOtherTextures: model not found for id', modelId);
    return;
  }

  if (typeof textureMap === 'string') {
    try {
      textureMap = JSON.parse(textureMap);
    } catch (e) {
      console.error('addOtherTextures: JSON parse error', e);
      return;
    }
  }

  if (typeof textureMap !== 'object' || textureMap === null || Array.isArray(textureMap)) {
    console.error('addOtherTextures: textureMap není objekt', textureMap);
    return;
  }

  const entries = Object.entries(textureMap);
  if (entries.length === 0) {
    return;
  }

  await Promise.all(entries.map(([id, url]) => {
    return new Promise((resolve) => {
      new THREE.TextureLoader().load(url, (texture) => {
        modelObject.otherTextures.push({ id, texture });
        resolve();
      }, undefined, (error) => {
        console.error(`Error loading texture for id ${id}:`, error);
        resolve();
      });
    });
  }));
}

/**
 * Odstraní texturu z modelu
 */
export async function removeOtherTexture(modelId, textureId, models, lastSelectedTextureId, switchToMainTextureFn) {
  const modelObject = models.find(m => m.id === modelId);

  if (!modelObject) {
    console.error('removeOtherTexture: model not found for id', modelId);
    return lastSelectedTextureId;
  }

  const index = modelObject.otherTextures.findIndex(t => t.id === textureId);
  if (index !== -1) {
    const [removed] = modelObject.otherTextures.splice(index, 1);
    if (removed && removed.texture) {
      try {
        removed.texture.dispose();
      } catch (e) { /* ignore */ }
    }

    if (lastSelectedTextureId === textureId) {
      await switchToMainTextureFn(modelId);
      return null;
    }
  }

  return lastSelectedTextureId;
}

/**
 * Přepne na jinou texturu
 */
export async function switchOtherTexture(modelId, textureId, currentModel, models, showModelByIdFn) {
  if (!currentModel || currentModel.id !== modelId || !currentModel.modelLoader) {
    const result = await showModelByIdFn(modelId);
    currentModel = result.model;
  }

  if (!currentModel || currentModel.id !== modelId || !currentModel.modelLoader) {
    console.error('switchOtherTexture: model not available after showModelById', modelId);
    return { model: currentModel, lastSelectedTextureId: null };
  }

  const textureObject = currentModel.otherTextures.find(t => t.id === textureId);
  if (textureObject && textureObject.texture) {
    const texture = textureObject.texture;
    currentModel.modelLoader.traverse((child) => {
      if (child.isMesh && texture) {
        child.material = new THREE.MeshStandardMaterial({ map: texture });
        child.material.needsUpdate = true;
      }
    });
    await new Promise(resolve => setTimeout(resolve, 50));
    return { model: currentModel, lastSelectedTextureId: textureId };
  } else {
    return { model: currentModel, lastSelectedTextureId: null };
  }
}

/**
 * Přepne na hlavní texturu
 */
export async function switchToMainTexture(modelId, currentModel, showModelByIdFn) {
  if (!currentModel || currentModel.id !== modelId || !currentModel.modelLoader) {
    const result = await showModelByIdFn(modelId);
    currentModel = result.model;
  }

  if (!currentModel || currentModel.id !== modelId || !currentModel.modelLoader) {
    console.error('switchToMainTexture: model not available', modelId);
    return { model: currentModel, lastSelectedTextureId: null };
  }

  const mainTextureUrl = currentModel.mainTexture;
  if (mainTextureUrl) {
    if (!currentModel.loadedMainTexture) {
      const textureLoader = new THREE.TextureLoader();
      try {
        currentModel.loadedMainTexture = await new Promise((resolve, reject) => {
          textureLoader.load(mainTextureUrl, (tex) => {
            tex.needsUpdate = true;
            resolve(tex);
          }, undefined, (err) => {
            console.error('Error loading main texture in switchToMainTexture:', err);
            reject(err);
          });
        });
      } catch (e) {
        console.error('switchToMainTexture: failed to load main texture', e);
        return { model: currentModel, lastSelectedTextureId: null };
      }
    }

    currentModel.modelLoader.traverse((child) => {
      if (child.isMesh) {
        child.material = new THREE.MeshStandardMaterial({ map: currentModel.loadedMainTexture });
        child.material.needsUpdate = true;
      }
    });

    await new Promise(resolve => setTimeout(resolve, 50));
    return { model: currentModel, lastSelectedTextureId: null };
  }

  return { model: currentModel, lastSelectedTextureId: null };
}

/**
 * Aplikuje masku na hlavní texturu
 */
export async function applyMaskToMainTexture(modelId, textureId, maskColor, currentModel, showModelByIdFn, renderFn) {
  if (!currentModel || currentModel.id !== modelId || !currentModel.modelLoader) {
    const result = await showModelByIdFn(modelId);
    currentModel = result.model;
  }

  let mainImage;
  let maskImage;

  try {
    if (!currentModel.loadedMainTexture) {
      const textureLoader = new THREE.TextureLoader();
      currentModel.loadedMainTexture = textureLoader.load(currentModel.mainTexture);
    }
    mainImage = currentModel.loadedMainTexture.image;

    const textureObject = currentModel.otherTextures.find(t => t.id === textureId);
    if (textureObject) {
      maskImage = textureObject.texture.image;
    } else {
      console.log(currentModel);
      console.error('Error getting texture image:', textureId);
    }
  } catch (err) {
    console.error('Error loading images:', err);
  }

  if (!mainImage || !maskImage) {
    return { model: currentModel, lastSelectedTextureId: textureId };
  }

  const width = mainImage.width;
  const height = mainImage.height;
  const resultCanvas = document.createElement('canvas');
  resultCanvas.width = width;
  resultCanvas.height = height;
  const ctx = resultCanvas.getContext('2d');
  ctx.drawImage(mainImage, 0, 0);

  const maskCanvas = document.createElement('canvas');
  maskCanvas.width = width;
  maskCanvas.height = height;
  const maskCtx = maskCanvas.getContext('2d');
  maskCtx.drawImage(maskImage, 0, 0);

  const mainImageData = ctx.getImageData(0, 0, width, height);
  const maskImageData = maskCtx.getImageData(0, 0, width, height);
  const mainData = new Uint8ClampedArray(mainImageData.data);
  const maskData = new Uint8ClampedArray(maskImageData.data);
  const maskColorRgb = hexToRgb(maskColor);

  const worker = new Worker(new URL('./textureMaskWorker.js', import.meta.url), { type: 'module' });

  await new Promise((resolve, reject) => {
    worker.onmessage = async function(e) {
      const { mainData: resultData } = e.data;
      for (let i = 0; i < resultData.length; i++) {
        mainImageData.data[i] = resultData[i];
      }
      ctx.putImageData(mainImageData, 0, 0);

      const resultTexture = new THREE.CanvasTexture(resultCanvas);
      if (currentModel) {
        currentModel.modelLoader.traverse((child) => {
          if (child.isMesh) {
            child.material.map = resultTexture;
            child.material.needsUpdate = true;
          }
        });
      }

      worker.terminate();
      resolve();
    };

    worker.onerror = function(e) {
      console.error('Worker error:', e);
      worker.terminate();
      reject(e);
    };

    worker.postMessage({ mainData, maskData, maskColorRgb, width, height });
  });

  renderFn();
  return { model: currentModel, lastSelectedTextureId: textureId };
}

/**
 * Převede hex barvu na RGB
 */
export function hexToRgb(hex) {
  hex = hex.replace('#', '');
  if (hex.length === 3) {
    hex = hex[0] + hex[0] + hex[1] + hex[1] + hex[2] + hex[2];
  }
  const num = parseInt(hex, 16);
  return {
    r: (num >> 16) & 255,
    g: (num >> 8) & 255,
    b: num & 255
  };
}

