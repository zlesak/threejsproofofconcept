package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;

public class ThreeJsFinishedActions  extends ComponentEvent<ThreeJsComponent> {
    public ThreeJsFinishedActions(ThreeJsComponent source) {
        super(source, false);
    }
}
