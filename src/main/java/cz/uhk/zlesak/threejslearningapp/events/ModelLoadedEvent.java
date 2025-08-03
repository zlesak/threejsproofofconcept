package cz.uhk.zlesak.threejslearningapp.events;


import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;


public class ModelLoadedEvent extends ComponentEvent<ThreeJsComponent> {
    public ModelLoadedEvent(ThreeJsComponent source) {
        super(source, false);
    }
}