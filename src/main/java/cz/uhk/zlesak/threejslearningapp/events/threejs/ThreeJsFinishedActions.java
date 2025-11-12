package cz.uhk.zlesak.threejslearningapp.events.threejs;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.common.ThreeJs;

public class ThreeJsFinishedActions  extends ComponentEvent<ThreeJs> {
    public ThreeJsFinishedActions(ThreeJs source) {
        super(source, false);
    }
}
