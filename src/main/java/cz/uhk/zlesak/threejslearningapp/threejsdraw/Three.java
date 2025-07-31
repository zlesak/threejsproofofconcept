package cz.uhk.zlesak.threejslearningapp.threejsdraw;

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
public class Three extends Component{

    private Runnable onDisposedCallback;

    public Three() {
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

    @ClientCallable
    public void modelLoadedEvent() {
        fireEvent(new ModelLoadedEvent(this));
    }

    public void addModelLoadedEventListener(ComponentEventListener<ModelLoadedEvent> listener) {
        addListener(ModelLoadedEvent.class, listener);
    }
}