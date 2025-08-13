package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableRunnable;
import cz.uhk.zlesak.threejslearningapp.events.ModelLoadedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

/**
 * This component integrates Three.js into a Vaadin application.
 * It allows for rendering 3D models and handling user interactions.
 * This class main purpose is to provide a bridge between the Java backend and the JavaScript Three.js library.
 */
@Slf4j
@JsModule("./js/three-javascript.js")
@NpmPackage(value = "three", version = "0.172.0")
@Tag("canvas")
@Scope("prototype")
public class ThreeJsComponent extends Component{

    private Runnable onDisposedCallback;

    /**
     * Default constructor for ThreeJsComponent.
     * Initializes the component via init() method.
     */
    public ThreeJsComponent() {
        init();
    }

    /**
     * Initializes the Three.js component by executing the JavaScript initialization function.
     * This method is called automatically when the component is created.
     * Further initialization is done in the JavaScript side, where the Three.js scene, camera, and renderer are set up.
     */
    public void init() {
        getElement().executeJs("""
            try {
                if (typeof window.initThree === 'function') {
                    window.initThree($0);
                }
            } catch (e) {
                console.error('[JS] Error in initThree:', e);
            }
            """, this);
    }

    /**
     * Disposes of the Three.js component.
     * This is crucial for cleaning up resources and preventing memory leaks and memory blockages.
     * It calls the JavaScript function to dispose of the Three.js scene and renderer.
     * After the disposal is complete, it triggers a server-side callback to notify that the component has been disposed of.
     * @param onDisposed a callback that will be executed after the component is disposed of.
     */
    public void dispose(SerializableRunnable onDisposed) {
        this.onDisposedCallback = onDisposed;
        getElement().executeJs("""
            window.disposeThree().then(() => {
                $0.$server.notifyDisposed();
            })
            """, this);
    }
/**
     * This method is called from the JavaScript side to notify the server that the component has been disposed of.
     * It executes the onDisposedCallback if it is set, allowing for any additional cleanup or actions to be performed after disposal.
     * There aro no other cleanup actions set up that would be needed to be done to properly dispose of the component as of now.
     */
    @ClientCallable
    private void notifyDisposed() {
        if (this.onDisposedCallback != null) {
            this.onDisposedCallback.run();
        }
    }

    /**
     * Executes a JavaScript action defined in the Three.js JavaScript module.
     * This method allows for interaction with the Three.js from the Java side.
     * The href parameter is expected to be a string that represents the action to be performed.
     * Mainly used to communicate user actions in Editor.js as click events that has the following action in the Three.js scene.
     *
     * @param href the action to be performed in the Three.js scene.
     */
    public void doAction(String href) { //TODO: Change method to appropriate method, or use switching for different actions when defined and available
        getElement().executeJs("""
        try {
            if (typeof window.doAction === 'function') {
                window.doAction($0);
            }
        } catch (e) {
            console.error('[JS] Error in doAction:', e);
        }
        """, href);
    }

    /**
     * Loads a 3D model into the Three.js scene.
     * This method expects a base64 encoded string of the model data.
     * It calls the JavaScript function loadModel to handle the loading process.
     * Loading methods are now separated into two methods, one for basic models and one for advanced models.
     * This allows for more flexibility in handling different types of models  based on the selected model upload method.
     *
     * @param base64Model the base64 encoded string of the model data.
     */
    public void loadModel(String base64Model) {
        getElement().executeJs("""
            try {
                if (typeof window.loadModel === 'function') {
                    window.loadModel($0);
                }
            } catch (e) {
                console.error('[JS] Error in loadModel:', e);
            }
            """, "data:application/octet-stream;base64," + base64Model);
    }

    /**
     * Loads an advanced 3D model into the Three.js scene.
     * This method expects two base64 encoded strings: one for the object data and one for the texture data.
     * It calls the JavaScript function loadAdvancedModel to handle the loading process.
     * This method is used for models that require both an object file and a texture file.
     * This allows for models to be loaded into the scene with multiple textures.
     * This loading methods needs only the main texture, as other may not be provided.
     * Other textures can be added later using the addOtherTexture method.
     *
     * @param objectUrl   the base64 encoded string of the object data.
     * @param textureUrl  the base64 encoded string of the texture data.
     */
    public void loadAdvancedModel(String objectUrl, String textureUrl) {
        getElement().executeJs("""
            try {
                if (typeof window.loadAdvancedModel === 'function') {
                    window.loadAdvancedModel($0, $1);
                }
            } catch (e) {
                console.error('[JS] Error in loadAdvancedModel:', e);
            }
            """, "data:application/octet-stream;base64," + objectUrl, "data:application/octet-stream;base64," + textureUrl);
    }

    /**
     * This method is called from the JavaScript side when the model is loaded.
     * It fires a ModelLoadedEvent to notify listeners that the model has been successfully loaded into the scene.
     * This is useful for triggering any actions that depend on the model being ready, such as updating the UI or enabling user interactions.
     */
    @ClientCallable
    public void modelLoadedEvent() { //TODO use to make the loading asynchronous as possible, so that the user can interact with the frontend while the model is loading
        fireEvent(new ModelLoadedEvent(this));
    }

    /**
     * Adds a listener for the ModelLoadedEvent.
     * This allows other components to react when a model is loaded into the Three.js scene.
     * The listener will be notified whenever the modelLoadedEvent method is called from the JavaScript side.
     *
     * @param listener the listener to be added for model loaded events.
     */
    public void addModelLoadedEventListener(ComponentEventListener<ModelLoadedEvent> listener) {
        addListener(ModelLoadedEvent.class, listener);
    }

    /**
     * Clears the Three.js scene by executing the clear function defined in the JavaScript module.
     * This method is used to remove all objects from the scene, effectively resetting it.
     * It is useful for starting fresh without reloading the entire component.
     */
    public void clear() {
        getElement().executeJs("""
            try {
                if (typeof window.clear === 'function') {
                    window.clear();
                }
            } catch (e) {
                console.error('[JS] Error in clearModel:', e);
            }
            """);
    }

    /**
     * Adds a texture to the Three.js scene.
     * This method expects a base64 encoded string of the texture data.
     * It calls the JavaScript function addTexture to handle the addition of the texture.
     * This is used to apply textures to models in the scene.
     *
     * @param base64Texture the base64 encoded string of the texture data.
     */
    public void addOtherTexture(String base64Texture) {
        getElement().executeJs("""
            try {
                if (typeof window.addOtherTexture === 'function') {
                    window.addOtherTexture($0);
                }
            } catch (e) {
                console.error('[JS] Error in addOtherTexture:', e);
            }
            """, "data:application/octet-stream;base64," + base64Texture);
    }

    /**
     * Switches the currently displayed texture to another texture in the Three.js scene.
     * This method calls the JavaScript function switchOtherTexture to handle the switching process.
     * It is used to change the texture of the currently selected model or object in the scene.
     */
    public void switchOtherTexture(){ //TODO ADD THE PARAMETER TO SWITCH BETWEEN TEXTURES String textureName
        getElement().executeJs("""
            try {
                if (typeof window.switchOtherTexture === 'function') {
                    window.switchOtherTexture();
                }
            } catch (e) {
                console.error('[JS] Error in switchOtherTexture:', e);
            }
            """);
    }

    /**
     * Switches the main texture of the currently displayed model in the Three.js scene.
     * This method calls the JavaScript function switchMainTexture to handle the switching process.
     * It is used to change the main texture of the currently selected model or object in the scene to quickly switch back from any other texture to the main one.
     */
    public void switchMainTexture(){
        getElement().executeJs("""
            try {
                if (typeof window.switchMainTexture === 'function') {
                    window.switchMainTexture();
                }
            } catch (e) {
                console.error('[JS] Error in switchToMainTexture:', e);
            }
            """);
    }

    /**
     * Applies a mask to the main texture of the currently displayed model in the Three.js scene.
     * This method calls the JavaScript function applyMaskToMainTexture to handle the masking process.
     * The maskColor parameter is expected to be a string representing the color to be applied as a mask.
     * This is used to visually modify the main texture by applying a color mask.
     * This is needed as the user can choose a color to be applied as a mask to the main texture based on the provided colours defining parts of the model.
     *
     * @param maskColor the color to be applied as a mask to the main texture.
     */
    public void applyMaskToMainTexture(String maskColor){
        getElement().executeJs("""
            try {
                if (typeof window.applyMaskToMainTexture === 'function') {
                    window.applyMaskToMainTexture($0);//TODO CHANGE AFTER PROPER DEBUG DONE AND DOD accomplished
                }
            } catch (e) {
                console.error('[JS] Error in applyMaskToMainTexture:', e);
            }
            """, maskColor);
    }

    /**
     * This method is called from the JavaScript side when a color is picked by the user.
     * As of now it logs the selected color to the console and can be used to trigger further actions based on the chosen color.
     * This is a precondition to functionality of exercises
     *
     * @param hexColor the selected color in hexadecimal format.
     */
    @ClientCallable
    public void onColorPicked(String hexColor) { //TODO implement proper functionality based on chosen logic of transferring color to the JAVA side when properly thought through
        log.info("Vybraná barva: {}", hexColor);
    }
}