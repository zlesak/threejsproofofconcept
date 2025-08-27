import * as THREE from 'three';
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OBJLoader, OrbitControls } from 'three/addons';

class ThreeTest {
  constructor() {
    this.element = null;
    this.camera = null;
    this.scene = null;
    this.renderer = null;
    this.controls = null;
    this.model = null;
    this.animationId = null;
    this.isAnimating = false;
    this.ambientLight = null;
    this.otherTextures = [];
    this.mainTexture = null;
    this.lastSelectedTextureId = null;
    this.actionQueue = [];
    this._resizeObserver = null;
  }

  init = (element) => {
    this.element = element;
    this.doingActions("Initializing Three.js");
    // Camera
    this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.25, 50);
    this.camera.position.set(-1.8, 0.6, 2.7);

    // Scene
    this.scene = new THREE.Scene();

    // Renderer
    this.renderer = new THREE.WebGLRenderer({
      antialias: true,
      canvas: this.element,
      powerPreference: 'low-power' // Important for integrated GPUs
    });

    // Ambient Light
    this.ambientLight = new THREE.AmbientLight(0xffffff, 1);
    this.scene.add(this.ambientLight);

    // Skybox
    this.scene.background = new THREE.CubeTextureLoader()
      .setPath('skybox/')
      .load(['px.bmp', 'nx.bmp', 'py.bmp', 'ny.bmp', 'pz.bmp', 'nz.bmp']);

    // Controls
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.configureControls();

    // Event listeners
    window.addEventListener('resize', this.onWindowResize);

    const parent = this.element?.parentElement;
    if (parent && window.ResizeObserver) {
      this._resizeObserver = new ResizeObserver(() => {
        this.onWindowResize();
      });
      this._resizeObserver.observe(parent);
    }

    this.onWindowResize();
    this.startAnimation();
    this.addClickListener();
    this.finishedActions();
  };

  configureControls = () => {
    this.controls.enabled = true;
    this.controls.minDistance = 2;
    this.controls.maxDistance = 10;
    this.controls.autoRotate = true;
    this.controls.enableZoom = true;
    this.controls.zoomToCursor = true;
    this.controls.target.set(0, 0, -0.2);
    this.controls.autoRotateSpeed = 0;
    this.controls.enableDamping = true;
    this.controls.dampingFactor = 0.2;
    this.controls.update();
  };

  render = () => {
    if (this.renderer && this.scene && this.camera) {
      this.renderer.render(this.scene, this.camera);
    }
  };

  animate = () => {
    if (!this.isAnimating) return;

    if (this.controls) {
      this.controls.update();
    }

    this.render();
    this.animationId = requestAnimationFrame(this.animate);
  };

  startAnimation = () => {
    if (this.isAnimating) return;
    this.isAnimating = true;
    this.animate();
  };

  stopAnimation = () => {
    this.isAnimating = false;
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
      this.animationId = null;
    }
    if (this.renderer) {
      this.renderer.setAnimationLoop(null);
    }
  };

  onWindowResize = () => {
    if (!this.element || !this.renderer || !this.camera) return;

    const canvasElement = this.element;
    const parent = canvasElement.parentElement;
    if (!parent) return;

    const parentRect = parent.getBoundingClientRect();
    const width = parent.clientWidth || parentRect.width || window.innerWidth;
    const height = parent.clientHeight || parentRect.height || Math.max(200, Math.floor(window.innerHeight * 0.5));

    canvasElement.width = width;
    canvasElement.height = height;

    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(width, height);
    this.render();
  };

  loadModel = async (modelUrl) => {
    this.doingActions("Loading model");
    if (!this.scene) return;
    if (this.model) {
      this.disposeObject(this.model);
      this.scene.remove(this.model);
      this.model = null;
    }
    const loader = new GLTFLoader();
    await new Promise((resolve, reject) => {
      loader.load(modelUrl,
        (gltf) => {
          this.model = gltf.scene;
          if (this.model.children[0]?.geometry) {
            this.model.children[0].geometry.center();
          }
          this.scene.add(this.model);
          this.centerCameraOnModel();
          resolve();
        },
        undefined,
        (error) => {
          console.error('Error loading model:', error);
          reject(error);
        }
      );
    });
    this.finishedActions();
  };

  centerCameraOnModel = () => {
    if (!this.model) return;
    const box = new THREE.Box3().setFromObject(this.model);
    const center = box.getCenter(new THREE.Vector3());
    const size = box.getSize(new THREE.Vector3());

    this.camera.position.copy(center);
    this.camera.position.x += size.length();
    this.camera.position.y += size.length() * 0.5;
    this.camera.position.z += size.length();
    this.camera.lookAt(center);

    this.controls.target.copy(center);
    this.controls.update();
    this.onWindowResize();
  };

  loadAdvancedModel = async (objUrl, mainTextureUrl) => {
    this.doingActions("Loading advanced model");
    const textureLoader = new THREE.TextureLoader();
    const mainTexture = textureLoader.load(mainTextureUrl);
    this.mainTexture = mainTexture;
    const objLoader = new OBJLoader();
    await new Promise((resolve, reject) => {
      objLoader.load(objUrl, (obj) => {
        obj.traverse((child) => {
          if (child.isMesh) {
            child.material = new THREE.MeshStandardMaterial({ map: mainTexture });
          }
        });
        this.model = obj;
        this.scene.add(this.model);
        this.centerCameraOnModel();
        resolve();
      }, undefined, (error) => {
        console.error('Error loading advanced model:', error);
        reject(error);
      });
    });
    this.finishedActions();
  };

  addOtherTextures = async (textureMap) => {
    this.doingActions("Adding other textures");
    if (typeof textureMap === 'string') {
      try {
        textureMap = JSON.parse(textureMap);
      } catch (e) {
        console.error('addOtherTextures: JSON parse error', e);
        this.finishedActions();
        return;
      }
    }
    if (typeof textureMap !== 'object' || textureMap === null || Array.isArray(textureMap)) {
      console.error('addOtherTextures: textureMap není objekt', textureMap);
      this.finishedActions();
      return;
    }
    const textureLoader = new THREE.TextureLoader();
    const entries = Object.entries(textureMap);
    if (entries.length === 0) {
      this.finishedActions();
      return;
    }
    await Promise.all(entries.map(([id, base64]) => {
      return new Promise((resolve) => {
        textureLoader.load(base64, (texture) => {
          this.otherTextures.push({ id, texture });
          resolve();
        }, undefined, (error) => {
          console.error(`Error loading texture for id ${id}:`, error);
          resolve();
        });
      });
    }));
    this.finishedActions();
  };

  switchOtherTexture = async (id) => {
    this.doingActions("Switching to other texture");
    if (!this.model || !this.otherTextures.length) {
      this.finishedActions();
      return;
    }
    const textureObject = this.otherTextures.find(t => t.id === id);
    if (textureObject) {
      const texture = textureObject.texture;
      this.model.traverse((child) => {
        if (child.isMesh && texture) {
          child.material.map = texture;
          child.material.needsUpdate = true;
        }
      });
      await new Promise(resolve => setTimeout(resolve, 100));
      this.finishedActions();
      this.render();
    } else {
      this.finishedActions();
      await this.switchToMainTexture();
    }
  };

  switchToMainTexture = async () => {
    this.doingActions("Switching to main texture");
    if (!this.model || !this.mainTexture) {
      this.finishedActions();
      return;
    }
    this.model.traverse((child) => {
      if (child.isMesh) {
        child.material.map = this.mainTexture;
        child.material.needsUpdate = true;
      }
    });
    await new Promise(resolve => setTimeout(resolve, 100));
    this.finishedActions();
    this.render();
  };

  dispose = () => {
    this.stopAnimation();
    if (this.renderer) {
      try {
        this.renderer.setAnimationLoop(null);
        if (this.renderer.forceContextLoss) {
          this.renderer.forceContextLoss();
        }
        this.renderer.dispose();
      } catch (e) {
        console.warn('Renderer dispose issue:', e);
      }
    }
    const canvas = this.renderer?.domElement || this.element;
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

    this.scene?.traverse(obj => {
      if (obj.material) {
        if (Array.isArray(obj.material)) {
          obj.material.forEach(m => {
            try { m.dispose(); m.needsUpdate = true; } catch(e) { /* ignore */ }
          });
        } else {
          try { obj.material.dispose(); obj.material.needsUpdate = true; } catch(e) { /* ignore */ }
        }
      }
      if (obj.geometry) {
        try { obj.geometry.dispose(); } catch(e) { /* ignore */ }
        obj.geometry = null;
      }
    });

    if (this._resizeObserver) {
      try { this._resizeObserver.disconnect(); } catch(e) { /* ignore */ }
      this._resizeObserver = null;
    }
  };

  disposeObject = (object) => {
    if (!object) return;

    if (object.geometry) {
      try { object.geometry.dispose(); } catch(e) { /* ignore */ }
    }

    if (object.material) {
      if (Array.isArray(object.material)) {
        object.material.forEach(material => this.disposeMaterial(material));
      } else {
        this.disposeMaterial(object.material);
      }
    }

    if (object.dispose) {
      try { object.dispose(); } catch(e) { /* ignore */ }
    }

    if (object.children) {
      object.children.forEach(child => this.disposeObject(child));
    }
  };

  disposeMaterial = (material) => {
    if (!material) return;

    try { material.dispose(); } catch(e) { /* ignore */ }

    for (const prop in material) {
      const value = material[prop];
      if (value && value.isTexture) {
        try { value.dispose(); } catch(e) { /* ignore */ }
      }
    }
  };

  clear = async () => {
    this.doingActions("Clearing scene");
    if (this.scene) {
      this.scene.traverse((obj) => {
        if (obj !== this.ambientLight && obj.type !== 'Scene' && obj.type !== 'CubeTexture') {
          if (obj.material) {
            this.disposeMaterial(obj.material);
          }
          if (obj.geometry) {
            try { obj.geometry.dispose(); } catch(e) { /* ignore */ }
          }
          this.scene.remove(obj);
        }
      });
      if (this.model) {
        this.disposeObject(this.model);
        this.scene.remove(this.model);
        this.model = null;
      }
    }
    await new Promise(resolve => setTimeout(resolve, 100));
    this.finishedActions();
    this.render();
  };

  getTextureColorAtClick = (event) => {
    if (!this.camera || !this.scene || !this.renderer) return;
    const rect = this.renderer.domElement.getBoundingClientRect();
    const mouse = new THREE.Vector2();
    mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
    mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

    const raycaster = new THREE.Raycaster();
    raycaster.setFromCamera(mouse, this.camera);
    const intersects = raycaster.intersectObjects(this.scene.children, true);
    if (intersects.length > 0) {
      const intersect = intersects[0];
      const uv = intersect.uv;
      const mesh = intersect.object;
      const texture = mesh.material?.map;
      if (uv && texture && texture.image) {
        const image = texture.image;
        const canvas = document.createElement('canvas');
        canvas.width = image.width;
        canvas.height = image.height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(image, 0, 0);
        const x = Math.floor(uv.x * image.width);
        const y = Math.floor((1 - uv.y) * image.height);
        const pixel = ctx.getImageData(x, y, 1, 1).data;
        const hex = '#' + ((1 << 24) | (pixel[0] << 16) | (pixel[1] << 8) | pixel[2]).toString(16).slice(1);

        if (this.element && this.element.$server && typeof this.element.$server.onColorPicked === 'function') {
          this.element.$server.onColorPicked(hex);
        }
      }
    }
    return null;
  };

  addClickListener = () => {
    if (this.renderer && this.renderer.domElement) {
      this.renderer.domElement.addEventListener('click', (event) => {
        this.getTextureColorAtClick(event);
      });
    }
  };

  applyMaskToMainTexture = async (id, maskColor) => {
    this.doingActions("Applying mask to texture");
    this.lastSelectedTextureId = id;
    const mainImage = this.mainTexture?.image;
    const textureObject = this.otherTextures.find(t => t.id === id);
    let maskImage;
    if (textureObject) {
      maskImage = textureObject.texture.image;
    } else {
      console.error('Error getting texture image:', id);
    }
    if (!mainImage || !maskImage) {
      this.finishedActions();
      return;
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
        if (this.model) {
          this.model.traverse((child) => {
            if (child.isMesh) {
              child.material.map = resultTexture;
              child.material.needsUpdate = true;
            }
          });
        }
        worker.terminate();
        resolve();
      }.bind(this);
      worker.onerror = function(e) {
        console.error('Worker error:', e);
        worker.terminate();
        reject(e);
      };
      worker.postMessage({ mainData, maskData, maskColorRgb, width, height });
    });
    this.render();
    this.finishedActions();
  }

  returnToLastSelectedTexture() {
    if (this.lastSelectedTextureId) {
      this.switchOtherTexture(this.lastSelectedTextureId);
    }
  }

  doingActions(description) {
    this.actionQueue.push(description);
    if (this.element && this.element.$server && typeof this.element.$server.doingActions === 'function') {
      this.element.$server.doingActions(this.actionQueue[this.actionQueue.length - 1]);
    }
  }

  finishedActions() {
    if (this.actionQueue.length > 0) {
      this.actionQueue.shift();
    }
    if (this.actionQueue.length === 0) {
      if (this.element && this.element.$server && typeof this.element.$server.finishedActions === 'function') {
        this.element.$server.finishedActions();
      }
    } else {
      if (this.element && this.element.$server && typeof this.element.$server.doingActions === 'function') {
        this.element.$server.doingActions(this.actionQueue[this.actionQueue.length - 1]);
      }
    }
  }
}

function hexToRgb(hex) {
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

// Multi-instance management
const instances = new WeakMap();

function getInstance(element) {
  return instances.get(element);
}

function setInstance(element, inst) {
  instances.set(element, inst);
}

window.initThree = function(element) {
  const existing = getInstance(element);
  if (existing) {
    try { existing.dispose(); } catch(e) { /* ignore */ }
  }
  const inst = new ThreeTest();
  setInstance(element, inst);
  inst.init(element);
};

window.disposeThree = function(element) {
  return new Promise((resolve) => {
    const inst = getInstance(element);
    if (inst) {
      try { inst.dispose(); } catch(e) { /* ignore */ }
      instances.delete(element);
      setTimeout(() => resolve(), 100);
    } else {
      resolve();
    }
  });
};

window.loadModel = function(element, modelUrl) {
  const inst = getInstance(element);
  if (inst) {
    inst.loadModel(modelUrl);
  }
};

window.loadAdvancedModel = function(element, objUrl, textureUrl) {
  const inst = getInstance(element);
  if (inst) {
    inst.loadAdvancedModel(objUrl, textureUrl);
  }
};

window.clear = function(element) {
  const inst = getInstance(element);
  if (inst) {
    inst.clear();
  }
};

window.addOtherTextures = function(element, textureJson) {
  const inst = getInstance(element);
  if (inst) {
    inst.addOtherTextures(textureJson);
  }
};

window.switchOtherTexture = function(element, id) {
  const inst = getInstance(element);
  if (inst) {
    inst.switchOtherTexture(id);
  }
};

window.returnToLastSelectedTexture = function(element) {
  const inst = getInstance(element);
  if (inst) {
    inst.returnToLastSelectedTexture();
  }
};

window.applyMaskToMainTexture = function(element, textureId, maskColor) {
  const inst = getInstance(element);
  if (inst) {
    inst.applyMaskToMainTexture(textureId, maskColor);
  }
};

window.addEventListener('beforeunload', () => {
});
