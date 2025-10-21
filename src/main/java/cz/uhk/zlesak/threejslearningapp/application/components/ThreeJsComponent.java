package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableRunnable;
import cz.uhk.zlesak.threejslearningapp.application.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.application.components.notifications.InfoNotification;
import cz.uhk.zlesak.threejslearningapp.application.events.ThreeJsDoingActions;
import cz.uhk.zlesak.threejslearningapp.application.events.ThreeJsFinishedActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * This component integrates Three.js into a Vaadin application.
 * It allows for rendering 3D models and handling user interactions.
 * This class main purpose is to provide a bridge between the Java backend and the JavaScript Three.js library.
 */
@Slf4j
@JsModule("./js/threejs/three-javascript.js")
@NpmPackage(value = "three", version = "0.172.0")
@Tag("canvas")
@Scope("prototype")
public class ThreeJsComponent extends Component {

    private Runnable onDisposedCallback;

    /**
     * Default constructor for ThreeJsComponent.
     */
    public ThreeJsComponent() {
        addAttachListener(e -> init());
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
                """, getElement());
    }

    /**
     * Disposes of the Three.js component.
     * This is crucial for cleaning up resources and preventing memory leaks and memory blockages.
     * It calls the JavaScript function to dispose of the Three.js scene and renderer.
     * After the disposal is complete, it triggers a server-side callback to notify that the component has been disposed of.
     *
     * @param onDisposed a callback that will be executed after the component is disposed of.
     */
    public void dispose(SerializableRunnable onDisposed) {
        this.onDisposedCallback = onDisposed;
        getElement().executeJs("""
                window.disposeThree($0).then(() => {
                    $1.$server.notifyDisposed();
                })
                """, getElement(), this);
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
     * Loads a 3D model into the Three.js scene.
     * This method expects a base64 encoded string of the model data.
     * It calls the JavaScript function loadModel to handle the loading process.
     * Loading methods are now separated into two methods, one for basic models and one for advanced models.
     * This allows for more flexibility in handling different types of models  based on the selected model upload method.
     *
     * @param modelUrl the base64 encoded string of the model data.
     */
    private void loadModel(String modelUrl) {
        getElement().executeJs("""
                try {
                    if (typeof window.loadModel === 'function') {
                        window.loadModel($0, $1);
                    }
                } catch (e) {
                    console.error('[JS] Error in loadModel:', e);
                }
                """, getElement(), modelUrl);
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
     * @param objectUrl  the base64 encoded string of the object data.
     * @param textureUrl the base64 encoded string of the texture data.
     */
    public void loadModel(String objectUrl, String textureUrl) {
        if (textureUrl == null || textureUrl.isBlank()) {
            loadModel(objectUrl);
        } else {
            getElement().executeJs("""
                    try {
                        if (typeof window.loadAdvancedModel === 'function') {
                            window.loadAdvancedModel($0, $1, $2);
                        }
                    } catch (e) {
                        console.error('[JS] Error in loadAdvancedModel:', e);
                    }
                    """, getElement(), objectUrl, textureUrl);
        }
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
                        window.clear($0);
                    }
                } catch (e) {
                    console.error('[JS] Error in clearModel:', e);
                }
                """, getElement());
    }

    /**
     * Adds a texture to the Three.js scene.
     * This method expects a base64 encoded string of the texture data.
     * It calls the JavaScript function addTexture to handle the addition of the texture.
     * This is used to apply textures to models in the scene.
     *
     * @param otherTexturesUrl the base64 encoded string of the texture data.
     */
    public void addOtherTextures(Map<String, String> otherTexturesUrl) {
        if (otherTexturesUrl.isEmpty()) return;
        String jsonTextures = new com.google.gson.Gson().toJson(otherTexturesUrl);
        getElement().executeJs("""
                try {
                    if (typeof window.addOtherTextures === 'function') {
                        window.addOtherTextures($0, $1);
                    }
                } catch (e) {
                    console.error('[JS] Error in addOtherTexture:', e);
                }
                """, getElement(), jsonTextures);
    }

    /**
     * Removes a texture from the Three.js scene based on its identifier.
     * This method calls the JavaScript function removeOtherTexture to handle the removal process.
     * It is used to delete textures that are no longer needed or to free up resources.
     *
     * @param id identification of the texture to be deleted
     * @see ModelUploadFormScrollerComposition for usage context
     */
    public void removeOtherTexture(String id){
        if (id.isEmpty()) return;
        getElement().executeJs("""
                try {
                    if (typeof window.removeOtherTexture === 'function') {
                        window.removeOtherTexture($0, $1);
                    }
                } catch (e) {
                    console.error('[JS] Error in removeOtherTexture:', e);
                }
                """, getElement(), id);
    }

    /**
     * Switches the currently displayed texture to another texture in the Three.js scene.
     * This method calls the JavaScript function switchOtherTexture to handle the switching process.
     * It is used to change the texture of the currently selected model or object in the scene.
     */
    public void switchOtherTexture(String textureId) {
        getElement().executeJs("""
                try {
                    if (typeof window.switchOtherTexture === 'function') {
                        window.switchOtherTexture($0, $1);
                    }
                } catch (e) {
                    console.error('[JS] Error in switchOtherTexture:', e);
                }
                """, getElement(), textureId);
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
    public void applyMaskToMainTexture(String textureId, String maskColor) {
        getElement().executeJs("""
                try {
                    if (typeof window.applyMaskToMainTexture === 'function') {
                        window.applyMaskToMainTexture($0, $1, $2);
                    }
                } catch (e) {
                    console.error('[JS] Error in applyMaskToMainTexture:', e);
                }
                """, getElement(), textureId, maskColor);
    }

    /**
     * Used to return to the last selected texture in the Three.js scene.
     * This method calls the JavaScript function returnToLastSelectedTexture to handle the process.
     * It is used to revert any changes made to the texture selection, allowing the user to go back to the previously selected texture.
     * This is useful in scenarios where the user wants to undo a texture change and return to the last known good state.
     * This is a precondition to functionality of exercises.
     */
    public void returnToLastSelectedTexture() {
        getElement().executeJs("""
                try {
                    if (typeof window.returnToLastSelectedTexture === 'function') {
                        window.returnToLastSelectedTexture($0);
                    }
                } catch (e) {
                    console.error('[JS] Error in returnToLastSelectedTexture:', e);
                }
                """, getElement());
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
        new InfoNotification("Vybraná barva: " + hexColor);
    }

    /**
     * This method is called from the JavaScript side when the renderer starts performing actions.
     */
    @ClientCallable
    public void doingActions(String actionDescription) {
        fireEvent(new ThreeJsDoingActions(this, actionDescription));
    }

    /**
     * This method is called from the JavaScript side when the renderer finishes performing actions.
     */
    @ClientCallable
    public void finishedActions() {
        fireEvent(new ThreeJsFinishedActions(this));
    }

    /**
     * Adds a listener for the ThreeJsDoingActions event.
     * This allows other components to react when the Three.js renderer starts performing actions.
     *
     * @param listener the listener to be added for ThreeJsDoingActions events.
     */
    public void addThreeJsDoingActionsListener(ComponentEventListener<ThreeJsDoingActions> listener) {
        addListener(ThreeJsDoingActions.class, listener);
    }

    /**
     * Adds a listener for the ThreeJsFinishedActions event.
     * This allows other components to react when the Three.js renderer finishes performing actions.
     *
     * @param listener the listener to be added for ThreeJsFinishedActions events.
     */
    public void addThreeJsFinishedActionsListener(ComponentEventListener<ThreeJsFinishedActions> listener) {
        addListener(ThreeJsFinishedActions.class, listener);
    }
}
