import * as THREE from 'three';
import { OrbitControls } from 'three/addons';

/**
 * Vytvoří a nakonfiguruje kameru
 */
export function createCamera() {
  const camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.25, 50);
  camera.position.set(-1.8, 0.6, 2.7);
  return camera;
}

/**
 * Vytvoří scénu se skyboxem
 */
export function createScene() {
  const scene = new THREE.Scene();

  // Skybox
  scene.background = new THREE.CubeTextureLoader()
    .setPath('skybox/')
    .load(['px.bmp', 'nx.bmp', 'py.bmp', 'ny.bmp', 'pz.bmp', 'nz.bmp']);

  return scene;
}

/**
 * Vytvoří renderer
 */
export function createRenderer(canvasElement) {
  const renderer = new THREE.WebGLRenderer({
    antialias: true,
    canvas: canvasElement,
    powerPreference: 'low-power'
  });
  return renderer;
}

/**
 * Vytvoří ambient light
 */
export function createAmbientLight() {
  return new THREE.AmbientLight(0xffffff, 1);
}

/**
 * Vytvoří a nakonfiguruje orbit controls
 */
export function createControls(camera, domElement) {
  const controls = new OrbitControls(camera, domElement);
  controls.enabled = true;
  controls.minDistance = 2;
  controls.maxDistance = 10;
  controls.autoRotate = true;
  controls.enableZoom = true;
  controls.zoomToCursor = true;
  controls.target.set(0, 0, -0.2);
  controls.autoRotateSpeed = 0;
  controls.enableDamping = true;
  controls.dampingFactor = 0.2;
  controls.update();
  return controls;
}

/**
 * Vycentruje kameru na model
 */
export function centerCameraOnModel(camera, controls, model) {
  if (!model.modelLoader) return;

  const box = new THREE.Box3().setFromObject(model.modelLoader);
  const center = box.getCenter(new THREE.Vector3());
  const size = box.getSize(new THREE.Vector3());

  camera.position.copy(center);
  camera.position.x += size.length();
  camera.position.y += size.length() * 0.5;
  camera.position.z += size.length();
  camera.lookAt(center);

  controls.target.copy(center);
  controls.update();
}

