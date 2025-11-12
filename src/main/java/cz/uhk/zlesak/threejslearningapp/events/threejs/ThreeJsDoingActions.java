package cz.uhk.zlesak.threejslearningapp.events.threejs;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.common.ThreeJs;
import lombok.Getter;

@Getter
public class ThreeJsDoingActions extends ComponentEvent<ThreeJs> {
    private final String description;

    public ThreeJsDoingActions(ThreeJs source, String description) {
        super(source, false);
        this.description = description;
    }
}
