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
    this.currentOtherTextureIndex = 0;
    this.mainTexture = null;
  }

  init = (element) => {
    this.element = element;

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

    // Initial setup
    this.onWindowResize();
    this.startAnimation();
  };

  configureControls = () => {
    this.controls.enabled = true;
    this.controls.minDistance = 2;
    this.controls.maxDistance = 10;
    this.controls.autoRotate = true;
    this.controls.enableZoom = true;
    this.controls.zoomToCursor = true;
    this.controls.target.set(0, 0, -0.2);
    this.controls.autoRotateSpeed = 1.0;
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

    canvasElement.width = parent.clientWidth;

    const modelDiv = document.getElementById('modelDiv').offsetHeight;
    console.log(modelDiv);
    canvasElement.height = modelDiv;

    this.camera.aspect = canvasElement.width / canvasElement.height;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(canvasElement.width, canvasElement.height);
    this.render();
  };

  loadModel = (modelUrl) => {
    if (!this.scene) return;

    // Clean up previous model
    if (this.model) {
      this.disposeObject(this.model);
      this.scene.remove(this.model);
      this.model = null;
    }

    const loader = new GLTFLoader();
    loader.load(modelUrl,
      (gltf) => {
        this.model = gltf.scene;

        // Center geometry if it exists
        if (this.model.children[0]?.geometry) {
          this.model.children[0].geometry.center();
        }

        this.scene.add(this.model);

        // Center camera on model
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

        if (this.element && this.element.$server && typeof this.element.$server.modelLoadedEvent === 'function') {
          // console.info('Model loaded successfully');
          this.element.$server.modelLoadedEvent();
        }
      },
      (xhr) => {
      },
      (error) => {
        console.error('Error loading model:', error);
      }
    );
  };

  loadAdvancedModel = (objUrl, mainTextureUrl) => {
    const textureLoader = new THREE.TextureLoader();
    const mainTexture = textureLoader.load(mainTextureUrl);
    this.mainTexture = mainTexture;

    const objLoader = new OBJLoader();
    objLoader.load(objUrl, (obj) => {
      obj.traverse((child) => {
        if (child.isMesh) {
          child.material = new THREE.MeshStandardMaterial({ map: mainTexture });
        }
      });
      this.model = obj;
      this.scene.add(this.model);

      // Center camera on model
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

      if (this.element && this.element.$server && typeof this.element.$server.modelLoadedEvent === 'function') {
        console.info('Advanced model loaded successfully');
        this.element.$server.modelLoadedEvent();
      }
    }, (xhr) => {
      console.log((xhr.loaded / xhr.total * 100) + '% loaded');

    }, (error) => {
      console.error('Error loading advanced model:', error);
    });
  };

  addOtherTexture = (textureUrl) => {
    const textureLoader = new THREE.TextureLoader();
    const newTexture = textureLoader.load(textureUrl, (texture) => {
      this.otherTextures.push(texture);
      this.switchOtherTexture(this.otherTextures.length - 1);
    }, undefined, (error) => {
      console.error('Error loading texture:', error);
    });
  };

  switchOtherTexture = () => {
    if (!this.model || !this.otherTextures.length) return;
    this.currentOtherTextureIndex = (this.currentOtherTextureIndex + 1) % this.otherTextures.length;
    this.model.traverse((child) => {
      if (child.isMesh && this.otherTextures[this.currentOtherTextureIndex]) {
        child.material.map = this.otherTextures[this.currentOtherTextureIndex];
        child.material.needsUpdate = true;
      }
    });
    this.render();
  };

  switchToMainTexture = () => {
    if (!this.model || !this.mainTexture) return;
    this.model.traverse((child) => {
      if (child.isMesh) {
        child.material.map = this.mainTexture;
        child.material.needsUpdate = true;
      }
    });
    this.render();
  };

  dispose = () => {
    this.stopAnimation();
    if (this.renderer) {
      this.renderer.setAnimationLoop(null);
      this.renderer.forceContextLoss();
    }
    this.renderer.dispose();
    const canvas = this.renderer?.domElement;
    if (canvas) {
      const contexts = ['webgl', 'experimental-webgl', 'webgl2'];
      contexts.forEach(ctx => {
        const gl = canvas.getContext(ctx);
        if (gl?.getExtension('WEBGL_lose_context')) {
          gl.getExtension('WEBGL_lose_context').loseContext();
          gl.getExtension('WEBGL_lose_context').restoreContext();
          gl.getExtension('WEBGL_lose_context').loseContext();
        }
      });
      canvas.width = 1;
      canvas.height = 1;
    }

    this.scene?.traverse(obj => {
      if (obj.material) {
        if (Array.isArray(obj.material)) {
          obj.material.forEach(m => {
            m.dispose();
            m.needsUpdate = true;
          });
        } else {
          obj.material.dispose();
          obj.material.needsUpdate = true;
        }
      }
      if (obj.geometry) {
        obj.geometry.dispose();
        obj.geometry = null;
      }
    });

    // Chrome only
    if (window.gc) {
      window.gc();
    }
  };

  disposeObject = (object) => {
    if (!object) return;

    // Geometry
    if (object.geometry) {
      object.geometry.dispose();
    }

    // Material
    if (object.material) {
      if (Array.isArray(object.material)) {
        object.material.forEach(material => this.disposeMaterial(material));
      } else {
        this.disposeMaterial(object.material);
      }
    }

    // Custom dispose method
    if (object.dispose) {
      object.dispose();
    }

    // Children
    if (object.children) {
      object.children.forEach(child => this.disposeObject(child));
    }
  };

  disposeMaterial = (material) => {
    if (!material) return;

    material.dispose();

    // Dispose textures
    for (const prop in material) {
      const value = material[prop];
      if (value && value.isTexture) {
        value.dispose();
      }
    }
  };

  changeSpeed = (speed) => {
    this.controls.autoRotateSpeed += speed;
  };

  clear = () => {
    if (this.scene) {
      this.scene.traverse((obj) => {
        if (obj !== this.ambientLight && obj.type !== 'Scene' && obj.type !== 'CubeTexture') {
          if (obj.material) {
            this.disposeMaterial(obj.material);
          }
          if (obj.geometry) {
            obj.geometry.dispose();
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
    this.render();
  };
}

// Global interface
let tt = null;

window.loadModel = function(modelUrl) {
  if (tt) {
    tt.loadModel(modelUrl);
  }
};

window.loadAdvancedModel = function(objUrl, textureUrl) {
  if (tt) {
    tt.loadAdvancedModel(objUrl, textureUrl);
  }
};

window.clear = function() {
  if (tt) {
    tt.clear();
  }
};

window.initThree = function(element) {
  if (tt) {
    tt.dispose();
  }
  tt = new ThreeTest();
  tt.init(element);
};

window.disposeThree = function() {
  return new Promise((resolve) => {
    if (tt) {
      tt.dispose();
      setTimeout(() => {
        tt = null;
        resolve();
      }, 100);
    } else {
      resolve();
    }
  });
};

window.addOtherTexture = function(textureUrl) {
  if (tt) {
    tt.addOtherTexture(textureUrl);
  }
};
window.switchOtherTexture = function(index) {
  if (tt) {
    tt.switchOtherTexture(index);
  }
};
window.switchMainTexture = function() {
  if (tt) {
    tt.switchToMainTexture();
  }
};

window.addEventListener('load', () => {
  if (tt && tt.element) {
    tt.onWindowResize();
  }
});

window.addEventListener('beforeunload', () => {
  window.disposeThree();
});

window.doAction = function(href) {
  console.log('doAction called from WysiwygE link:', href); //TODO: Remove after debugging phase
  tt.changeSpeed(0.3); //TODO: Change to certain logic when available, now for channel testing speed up setting
};
