package cz.uhk.zlesak.threejslearningapp.threejsdraw;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.function.SerializableRunnable;

@JsModule("./js/three-javascript.js")
@NpmPackage(value = "three", version = "0.172.0")
@Tag("canvas")
public class Three extends Component{

    private Runnable onDisposedCallback;

    public Three() {
        getElement().executeJs("window.initThree($0)", this);
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

    @ClientCallable
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
}