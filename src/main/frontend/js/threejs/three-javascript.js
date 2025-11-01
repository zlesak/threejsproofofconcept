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
    this.models = [];
    this.animationId = null;
    this.isAnimating = false;
    this.ambientLight = null;
    this.lastSelectedTextureId = null;
    this.actionQueue = [];
    this._resizeObserver = null;
    this.DEBUG_IMAGE = false;

  }

  init = (element) => {
    this.element = element;
    this.doingActions('Initializing Three.js');
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

  centerCameraOnModel = () => {
    if (!this.model.modelLoader) return;
    const box = new THREE.Box3().setFromObject(this.model.modelLoader);
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

  loadModel = async (modelUrl, modelId) => {
    this.doingActions('Loading model');
    if (!this.models.some(m => m.id === modelId)) {
      this.models.push({
        id: modelId,
        advanced: false,
        model: modelUrl,
        mainTexture: null,
        otherTextures: [],
        modelLoader: null,
        textureLoader: null
      });
    }
    this.finishedActions();
  };

  loadAdvancedModel = async (objUrl, mainTextureUrl, modelId) => {
    this.doingActions('Loading advanced model');
    if (!this.models.some(m => m.id === modelId)) {
      this.models.push({
        id: modelId,
        advanced: true,
        model: objUrl,
        mainTexture: mainTextureUrl,
        otherTextures: [],
        modelLoader: null,
        textureLoader: null,
        loadedMainTexture: null
      });
    }
    this.finishedActions();
  };

  showModelById = async (modelId) => {
    this.scene.children;
    this.doingActions('Switching model');
    const modelObject = this.models.find(m => m.id === modelId);
    if (this.model && this.model === modelObject) {
      this.finishedActions();
      return;
    }
    if (modelObject) {
      if (this.model) {
        this.disposeObject(this.model.modelLoader);
        try {
          this.scene.remove(this.model.modelLoader);
        } catch (e) { /* ignore */
        }
        this.model = null;
        this.lastSelectedTextureId = null;
      }
      this.model = modelObject;

      if (modelObject.advanced) {
        if (!modelObject.loadedMainTexture && modelObject.mainTexture) {
          const textureLoader = new THREE.TextureLoader();
          try {
            modelObject.loadedMainTexture = await new Promise((resolve, reject) => {
              textureLoader.load(modelObject.mainTexture, (texture) => {
                texture.needsUpdate = true;
                resolve(texture);
              }, undefined, (err) => {
                console.error('Error loading main texture:', err);
                reject(err);
              });
            });
          } catch (e) {
            console.error('Failed to load main texture for advanced model:', e);
            modelObject.loadedMainTexture = null;
          }
        }

        const objLoader = new OBJLoader();
        await new Promise((resolve, reject) => {
          objLoader.load(modelObject.model, (obj) => {
            obj.traverse((child) => {
              if (child.isMesh) {
                child.material = new THREE.MeshStandardMaterial({ map: modelObject.loadedMainTexture });
                child.material.needsUpdate = true;
              }
            });
            this.model.modelLoader = obj;
            this.scene.add(this.model.modelLoader);
            this.centerCameraOnModel();
            resolve();
          }, undefined, (error) => {
            console.error('Error loading advanced model:', error);
            reject(error);
          });
        });
      } else {
        const loader = new GLTFLoader();
        await new Promise((resolve, reject) => {
          loader.load(modelObject.model,
            (gltf) => {
              this.model.modelLoader = gltf.scene;
              if (this.model.modelLoader && this.model.modelLoader.children[0]?.geometry) {
                try {
                  this.model.modelLoader.children[0].geometry.center();
                } catch (e) { /* ignore */
                }
              }
              this.scene.add(this.model.modelLoader);
              this.centerCameraOnModel();
              resolve();
            },
            undefined,
            (error) => {
              console.error('Error loading basic model:', error);
              reject(error);
            }
          );
        });
      }

      await new Promise(resolve => setTimeout(resolve, 100));
      this.finishedActions();
      this.render();
    } else {
      this.finishedActions();
    }
  };

  addOtherTextures = async (textureMap, modelId) => {
    this.doingActions('Adding other textures');
    const modelObject = this.models.find(m => m.id === modelId);
    if (modelObject) {
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
      const entries = Object.entries(textureMap);
      if (entries.length === 0) {
        this.finishedActions();
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
    } else {
      console.error('addOtherTextures: model not found for id', modelId);
    }
    this.finishedActions();
  };
  removeOtherTexture = async (modelId, textureId) => {
    this.doingActions('Removing texture');
    const modelObject = this.models.find(m => m.id === modelId);
    if (modelObject) {
      const index = modelObject.otherTextures.findIndex(t => t.id === textureId);
      if (index !== -1) {
        const [removed] = modelObject.otherTextures.splice(index, 1);
        if (removed && removed.texture) {
          try {
            removed.texture.dispose();
          } catch (e) { /* ignore */
          }
        }
        if (this.lastSelectedTextureId === textureId) {
          this.lastSelectedTextureId = null;
          await this.switchToMainTexture(modelId);
        }
      }
    } else {
      console.error('removeOtherTexture: model not found for id', modelId);
    }
    this.finishedActions();
  };

  switchOtherTexture = async (modelId, textureId) => {
    this.scene.children;
    this.doingActions('Switching to other texture');

    if (!this.model || this.model.id !== modelId || !this.model.modelLoader) {
      await this.showModelById(modelId);
    }

    if (!this.model || this.model.id !== modelId || !this.model.modelLoader) {
      console.error('switchOtherTexture: model not available after showModelById', modelId);
      this.finishedActions();
      return;
    }

    const textureObject = this.model.otherTextures.find(t => t.id === textureId);
    if (textureObject && textureObject.texture) {
      const texture = textureObject.texture;
      this.model.modelLoader.traverse((child) => {
        if (child.isMesh && texture) {
          child.material = new THREE.MeshStandardMaterial({ map: texture });
          child.material.needsUpdate = true;
        }
      });
      await new Promise(resolve => setTimeout(resolve, 50));
      this.lastSelectedTextureId = textureId;
      this.finishedActions();
      this.render();
    } else {
      this.finishedActions();
      await this.switchToMainTexture(modelId);
    }
  };

  switchToMainTexture = async (modelId) => {
    this.doingActions('Switching to main texture');

    if (!this.model || this.model.id !== modelId || !this.model.modelLoader) {
      await this.showModelById(modelId);
    }

    if (!this.model || this.model.id !== modelId || !this.model.modelLoader) {
      console.error('switchToMainTexture: model not available', modelId);
      this.finishedActions();
      return;
    }

    const mainTextureUrl = this.model.mainTexture;
    if (mainTextureUrl) {
      if (!this.model.loadedMainTexture) {
        const textureLoader = new THREE.TextureLoader();
        try {
          this.model.loadedMainTexture = await new Promise((resolve, reject) => {
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
          this.finishedActions();
          return;
        }
      }

      this.model.modelLoader.traverse((child) => {
        if (child.isMesh) {
          child.material = new THREE.MeshStandardMaterial({ map: this.model.loadedMainTexture });
          child.material.needsUpdate = true;
        }
      });

      this.lastSelectedTextureId = null;
      await new Promise(resolve => setTimeout(resolve, 50));
      this.finishedActions();
      this.render();
    } else {
      this.finishedActions();
    }
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
        } catch (e) { /* ignore */
        }
      });
      canvas.width = 1;
      canvas.height = 1;
    }

    this.scene?.traverse(obj => {
      if (obj.material) {
        if (Array.isArray(obj.material)) {
          obj.material.forEach(m => {
            try {
              m.dispose();
              m.needsUpdate = true;
            } catch (e) { /* ignore */
            }
          });
        } else {
          try {
            obj.material.dispose();
            obj.material.needsUpdate = true;
          } catch (e) { /* ignore */
          }
        }
      }
      if (obj.geometry) {
        try {
          obj.geometry.dispose();
        } catch (e) { /* ignore */
        }
        obj.geometry = null;
      }
    });

    if (this._resizeObserver) {
      try {
        this._resizeObserver.disconnect();
      } catch (e) { /* ignore */
      }
      this._resizeObserver = null;
    }
  };

  disposeObject = (object) => {
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
        object.material.forEach(material => this.disposeMaterial(material));
      } else {
        this.disposeMaterial(object.material);
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
      object.children.forEach(child => this.disposeObject(child));
    }
  };

  disposeMaterial = (material) => {
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
  };

  clear = async () => {
    this.doingActions('Clearing scene');
    if (this.scene) {
      this.scene.traverse((obj) => {
        if (obj !== this.ambientLight && obj.type !== 'Scene' && obj.type !== 'CubeTexture') {
          if (obj.material) {
            this.disposeMaterial(obj.material);
          }
          if (obj.geometry) {
            try {
              obj.geometry.dispose();
            } catch (e) {
              console.error('Error disposing geometry during clear:', e);
            }
          }
          this.scene.remove(obj);
        }
      });
      if (this.model) {
        this.disposeObject(this.model.modelLoader);
        this.scene.remove(this.model.modelLoader);
        this.model = null;
      }
    }
    await new Promise(resolve => setTimeout(resolve, 100));
    this.finishedActions();
    this.render();
  };

  getTextureColorAtClick = (event) => {
    if (!this.camera || !this.scene || !this.renderer) return;
    if (this.lastSelectedTextureId === null || this.model === null) {
      return;
    }

    const rect = this.renderer.domElement.getBoundingClientRect();
    const mouse = new THREE.Vector2();
    mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
    mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
    const raycaster = new THREE.Raycaster();
    raycaster.setFromCamera(mouse, this.camera);
    const intersects = raycaster.intersectObjects(this.scene.children, true);
    if (intersects.length > 0) {
      const intersect = intersects[intersects.length - 1];
      const uv = intersect.uv;
      const mesh = intersect.object;
      const texture = mesh.material?.map;
      let image = null;

      if (texture instanceof THREE.CanvasTexture) {
        image = texture.image;
      } else if (texture instanceof THREE.Texture && texture.image) {
        image = texture.image;
      } else if (texture?.source?.data) {
        image = texture.source.data;
      }
      if (uv && texture && image) {
        if (this.DEBUG_IMAGE && image) {
          const maxDim = 300;
          let scale = 1;
          if (image.width > maxDim || image.height > maxDim) {
            scale = Math.min(maxDim / image.width, maxDim / image.height);
          }
          const displayWidth = Math.round(image.width * scale);
          const displayHeight = Math.round(image.height * scale);

          let wrapper = document.getElementById('debug-texture-wrapper');
          if (!wrapper) {
            wrapper = document.createElement('div');
            wrapper.id = 'debug-texture-wrapper';
            document.body.appendChild(wrapper);
          }
          wrapper.innerHTML = '';
          wrapper.style.position = 'fixed';
          wrapper.style.top = '10px';
          wrapper.style.left = '10px';
          wrapper.style.zIndex = 9999;
          wrapper.style.border = '2px solid red';
          wrapper.style.background = 'white';
          wrapper.style.overflow = 'auto';
          wrapper.style.maxWidth = maxDim + 'px';
          wrapper.style.maxHeight = maxDim + 'px';

          const displayCanvas = document.createElement('canvas');
          displayCanvas.width = displayWidth;
          displayCanvas.height = displayHeight;
          displayCanvas.style.width = displayWidth + 'px';
          displayCanvas.style.height = displayHeight + 'px';
          displayCanvas.style.display = 'block';
          displayCanvas.style.margin = '0 auto';

          const displayCtx = displayCanvas.getContext('2d');
          displayCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, displayWidth, displayHeight);
          wrapper.appendChild(displayCanvas);
        }

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
          this.element.$server.onColorPicked(this.model.id, this.lastSelectedTextureId, hex);
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

  applyMaskToMainTexture = async (modelId, textureId, maskColor) => {
    this.doingActions('Applying mask to texture');

    if (!this.model || this.model.id !== modelId || !this.model.modelLoader) {
      await this.showModelById(modelId);
    }

    this.lastSelectedTextureId = textureId;
    let mainImage;
    let maskImage;
    try {

      if (!this.model.loadedMainTexture) {
        const textureLoader = new THREE.TextureLoader();
        this.model.loadedMainTexture = textureLoader.load(this.model.mainTexture);
      }
      mainImage = this.model.loadedMainTexture.image;
      const textureObject = this.model.otherTextures.find(t => t.id === textureId);
      if (textureObject) {
        maskImage = textureObject.texture.image;
      } else {
        console.log(this.model);
        console.error('Error getting texture image:', textureId);
      }
    } catch (err) {
      console.error('Error loading images:', err);
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
          this.model.modelLoader.traverse((child) => {
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
  };

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
    try {
      existing.dispose();
    } catch (e) { /* ignore */
    }
  }
  const inst = new ThreeTest();
  setInstance(element, inst);
  inst.init(element);
};

window.disposeThree = function(element) {
  return new Promise((resolve) => {
    const inst = getInstance(element);
    if (inst) {
      try {
        inst.dispose();
      } catch (e) { /* ignore */
      }
      instances.delete(element);
      setTimeout(() => resolve(), 100);
    } else {
      resolve();
    }
  });
};

window.loadModel = async function(element, modelUrl, modelId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.loadModel(modelUrl, modelId);
  }
};

window.loadAdvancedModel = async function(element, objUrl, textureUrl, modelId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.loadAdvancedModel(objUrl, textureUrl, modelId);
  }
};

window.clear = async function(element) {
  const inst = getInstance(element);
  if (inst) {
    await inst.clear();
  }
};

window.addOtherTextures = async function(element, textureJson, modelId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.addOtherTextures(textureJson, modelId);
  }
};

window.removeOtherTexture = async function(element, modelId, textureId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.removeOtherTexture(modelId, textureId);
  }
};

window.switchOtherTexture = async function(element, modelId, textureId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.switchOtherTexture(modelId, textureId);
  }
};

window.showModel = async function(element, modelId) {
  const inst = getInstance(element);
  if (inst) {
    await inst.showModelById(modelId);
  }
};

window.applyMaskToMainTexture = async function(element, modelId, textureId, maskColor) {
  const inst = getInstance(element);
  if (inst) {
    await inst.applyMaskToMainTexture(modelId, textureId, maskColor);
  }
};
window.addEventListener('beforeunload', () => {
});
