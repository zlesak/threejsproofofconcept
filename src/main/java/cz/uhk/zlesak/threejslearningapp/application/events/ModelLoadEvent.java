package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

/**
 * Event representing the loading of a model in the application.
 * This event is fired when a model is loaded into the view or context.
 * It carries the base64-encoded model and texture data.
 */
@Getter
public class ModelLoadEvent extends ComponentEvent<Component> {
    private final String model;
    private final String texture;

    /**
     * Constructor for ModelLoadEvent.
     *
     * @param source        the source component that fired the event
     * @param model   the base64-encoded model data
     * @param texture the base64-encoded texture data
     */
    public ModelLoadEvent(Component source, String model, String texture) {
        super(source, false);
        this.model = model;
        this.texture = texture;
    }

}

