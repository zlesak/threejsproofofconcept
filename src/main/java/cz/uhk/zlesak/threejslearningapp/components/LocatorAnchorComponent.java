package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.dom.DomEventListener;
import elemental.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

/**
 * A component that represents an anchor element used for scrolling to a specific target in EditorJs.
 **/
@Slf4j
@Scope("prototype")
public class LocatorAnchorComponent extends Anchor {
    /**
     * Constructor that initializes the LocatorAnchorComponent with the provided content data and ID.
     * It sets the anchor's href to "#" and adds a click listener, allowing for custom scrolling behavior to the target element specified by contentDataId.
     * @param contentData the JSON object containing the content data for the anchor, including text and other attributes.
     * @param contentDataId the ID of the target element to scroll to when the anchor is clicked.
     * @param scrollClickListener the listener that handles the click event for scrolling to the target element.
     */
    public LocatorAnchorComponent(JsonObject contentData, String contentDataId, DomEventListener scrollClickListener) {
        this.setHref("#");
        this.setText(contentData.getString("text"));
        this.setWidthFull();
        this.getStyle().set("display", "block");
        this.getElement().setAttribute("data-target-id", contentDataId);
        this.getElement().addEventListener("click", scrollClickListener)
                .addEventData("event.preventDefault()");
    }
}
