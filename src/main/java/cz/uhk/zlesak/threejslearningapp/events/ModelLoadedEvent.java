package cz.uhk.zlesak.threejslearningapp.events;


import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;

/**
 * Event that is fired when a 3D model is loaded in the ThreeJsComponent.
 * This event is used to trigger actions in the UI after the model is successfully loaded.
 */
public class ModelLoadedEvent extends ComponentEvent<ThreeJsComponent> {
    public ModelLoadedEvent(ThreeJsComponent source) {
        super(source, false);
    }
}