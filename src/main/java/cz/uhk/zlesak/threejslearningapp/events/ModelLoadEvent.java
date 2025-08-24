package cz.uhk.zlesak.threejslearningapp.events;

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
    private final String base64Model;
    private final String base64Texture;

    /**
     * Constructor for ModelLoadEvent.
     *
     * @param source        the source component that fired the event
     * @param base64Model   the base64-encoded model data
     * @param base64Texture the base64-encoded texture data
     */
    public ModelLoadEvent(Component source, String base64Model, String base64Texture) {
        super(source, false);
        this.base64Model = base64Model;
        this.base64Texture = base64Texture;
    }

}

