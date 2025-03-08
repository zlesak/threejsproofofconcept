import * as THREE from "three";
import {GLTFLoader} from 'three/addons/loaders/GLTFLoader.js';
import {OrbitControls} from "three/addons";


let element, camera, scene, renderer, controls, model,tt;

class ThreeTest {
  init = (e) => {
//element to draw to (some form of canvas to draw on)
    element = e;
    camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.25, 50);
    camera.position.set( - 1.8, 0.6, 2.7 );
//scene
    scene = new THREE.Scene();
//renderer
    renderer = new THREE.WebGLRenderer({antialias: true, canvas: element});
    renderer.setAnimationLoop(this.animate);
//ambient light
    const ambientLight = new THREE.AmbientLight(0xffffff, 1); // Soft light
    scene.add(ambientLight);
//skybox
    scene.background = new THREE.CubeTextureLoader()
        .setPath( 'skybox/' )
        .load(['px.bmp','nx.bmp','py.bmp','ny.bmp','pz.bmp','nz.bmp']);
//model loader
//     const manager = new THREE.LoadingManager();
//     manager.onProgress = (url, loaded, total)=>{
//       console.log((loaded/total));
//     };
//     manager.onLoad = ()=>{
//     };
    const loader = new GLTFLoader();
    loader.load("models/model-cast-mozku/textura_4k/1mil_faces/1mil_faces.glb", async (gltf) => {
      model = gltf.scene;
      model.children[0].geometry.center();
      await renderer.compileAsync(model, camera, scene);
      scene.add(model);
      this.animate();
    }, (xhr) => {
//progress bar
      document.getElementById("chapter-container").$server.updateProgress(xhr.loaded/xhr.total);
      console.log(xhr.loaded/xhr.total);
      if(xhr.loaded === xhr.total){
        this.onWindowResize();
        document.getElementById("chapter-container").$server.hideProgressBar();
      }
    }, (error) => {
      console.error(error);
    });

//controls
    controls = new OrbitControls( camera, renderer.domElement );
    controls.addEventListener( 'change', this.render ); // use if there is no animation loop
    controls.enabled = true;
    controls.minDistance = 2;
    controls.maxDistance = 10;
    controls.autoRotate = true;
    controls.enableZoom = true;
    controls.zoomToCursor = true;

    controls.target.set(0,0,-0.2);

    controls.autoRotate = true;
    controls.autoRotateSpeed = 1.0;

    controls.enableDamping = true;
    controls.dampingFactor = 0.2;
    controls.update()

//event listeners
    window.addEventListener('resize', this.onWindowResize);
//apply changes and raw through animation
  }

  render = ()=> {
    renderer.render(scene, camera);
  }
  rotate = ()=>{
      model.rotation.x += 0.01;
      model.rotation.y += 0.01;
  }
  animate = () => {
    // this.rotate();
    controls.update();
    this.render();
  }
  onWindowResize = () => {
    let canvasElement = (document.getElementsByTagName("canvas"))[0];
    canvasElement.width = canvasElement.parentElement.clientWidth;
    let padding =  window.getComputedStyle(canvasElement.parentElement).getPropertyValue("padding-top");
    padding = padding.substring(0,padding.indexOf("p"));
    canvasElement.height = window.innerHeight - document.getElementsByTagName("header")[0].offsetHeight - 4*padding;
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(canvasElement.width, canvasElement.height);
    this.render();
  }
}

window.initThree = function(element) {
  tt = new ThreeTest();
  tt.init(element);
  tt.render()
};

window.addEventListener('load', ()=>{
  renderer.setSize(element.parentElement.clientWidth, element.parentElement.clientHeight);
  tt.onWindowResize();
});