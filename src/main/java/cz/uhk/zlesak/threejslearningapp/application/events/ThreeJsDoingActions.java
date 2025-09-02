package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.application.components.ThreeJsComponent;
import lombok.Getter;

@Getter
public class ThreeJsDoingActions extends ComponentEvent<ThreeJsComponent> {
    private final String description;

    public ThreeJsDoingActions(ThreeJsComponent source, String description) {
        super(source, false);
        this.description = description;
    }
}
