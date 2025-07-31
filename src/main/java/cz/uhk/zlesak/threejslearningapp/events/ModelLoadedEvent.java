package cz.uhk.zlesak.threejslearningapp.events;


import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;


public class ModelLoadedEvent extends ComponentEvent<Three> {
    public ModelLoadedEvent(Three source) {
        super(source, false);
    }
}