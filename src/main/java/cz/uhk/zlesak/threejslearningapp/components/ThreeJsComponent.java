package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableRunnable;
import cz.uhk.zlesak.threejslearningapp.events.ModelLoadedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import org.springframework.context.annotation.Scope;

@JsModule("./js/three-javascript.js")
@NpmPackage(value = "three", version = "0.172.0")
@Tag("canvas")
@Scope("prototype")
public class ThreeJsComponent extends Component{

    private Runnable onDisposedCallback;

    public ThreeJsComponent() {
        init();
    }

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

    public void dispose(SerializableRunnable onDisposed) {
        this.onDisposedCallback = onDisposed;
        getElement().executeJs("""
            window.disposeThree().then(() => {
                $0.$server.notifyDisposed();
            })
            """, this);
    }

    @ClientCallable
    private void notifyDisposed() {
        if (this.onDisposedCallback != null) {
            this.onDisposedCallback.run();
        }
    }

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

    @ClientCallable
    public void modelLoadedEvent() {
        fireEvent(new ModelLoadedEvent(this));
    }

    public void addModelLoadedEventListener(ComponentEventListener<ModelLoadedEvent> listener) {
        addListener(ModelLoadedEvent.class, listener);
    }

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
    public void switchOtherTexture(){
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
}