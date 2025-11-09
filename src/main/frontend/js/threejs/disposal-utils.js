/**
 * Dispose objektu a všech jeho potomků
 */
export function disposeObject(object) {
  if (!object) return;

  if (object.geometry) {
    try {
      object.geometry.dispose();
    } catch (e) {
      console.error('Error disposing geometry:', e);
    }
  }

  if (object.material) {
    if (Array.isArray(object.material)) {
      object.material.forEach(material => disposeMaterial(material));
    } else {
      disposeMaterial(object.material);
    }
  }

  if (object.dispose) {
    try {
      object.dispose();
    } catch (e) {
      console.error('Error disposing object:', e);
    }
  }

  if (object.children) {
    object.children.forEach(child => disposeObject(child));
  }
}

/**
 * Dispose materiálu a jeho textur
 */
export function disposeMaterial(material) {
  if (!material) return;

  try {
    material.dispose();
  } catch (e) {
    console.error('Error disposing material:', e);
  }

  for (const prop in material) {
    const value = material[prop];
    if (value && value.isTexture) {
      try {
        value.dispose();
      } catch (e) {
        console.error('Error disposing texture:', e);
      }
    }
  }
}

/**
 * Vyčistí celou scénu
 */
export function clearScene(scene, ambientLight, currentModel, disposeObjectFn) {
  if (!scene) return null;

  scene.traverse((obj) => {
    if (obj !== ambientLight && obj.type !== 'Scene' && obj.type !== 'CubeTexture') {
      if (obj.material) {
        disposeMaterial(obj.material);
      }
      if (obj.geometry) {
        try {
          obj.geometry.dispose();
        } catch (e) {
          console.error('Error disposing geometry during clear:', e);
        }
      }
      scene.remove(obj);
    }
  });

  if (currentModel) {
    disposeObjectFn(currentModel.modelLoader);
    scene.remove(currentModel.modelLoader);
    return null;
  }

  return currentModel;
}

/**
 * Dispose rendereru a uvolní WebGL kontext
 */
export function disposeRenderer(renderer) {
  if (!renderer) return;

  try {
    renderer.setAnimationLoop(null);
    if (renderer.forceContextLoss) {
      renderer.forceContextLoss();
    }
    renderer.dispose();
  } catch (e) {
    console.warn('Renderer dispose issue:', e);
  }

  const canvas = renderer.domElement;
  if (canvas) {
    const contexts = ['webgl', 'experimental-webgl', 'webgl2'];
    contexts.forEach(ctx => {
      try {
        const gl = canvas.getContext(ctx);
        const ext = gl && gl.getExtension && gl.getExtension('WEBGL_lose_context');
        if (ext) {
          ext.loseContext();
        }
      } catch (e) { /* ignore */ }
    });
    canvas.width = 1;
    canvas.height = 1;
  }
}

/**
 * Dispose všech materiálů ve scéně
 */
export function disposeSceneMaterials(scene) {
  if (!scene) return;

  scene.traverse(obj => {
    if (obj.material) {
      if (Array.isArray(obj.material)) {
        obj.material.forEach(m => {
          try {
            m.dispose();
            m.needsUpdate = true;
          } catch (e) { /* ignore */ }
        });
      } else {
        try {
          obj.material.dispose();
          obj.material.needsUpdate = true;
        } catch (e) { /* ignore */ }
      }
    }
    if (obj.geometry) {
      try {
        obj.geometry.dispose();
      } catch (e) { /* ignore */ }
      obj.geometry = null;
    }
  });
}

