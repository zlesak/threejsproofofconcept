package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.dom.DomEventListener;
import elemental.json.JsonObject;

public class LocatorAnchor extends Anchor {
    public LocatorAnchor (JsonObject contentData, String contentDataId, DomEventListener scrollClickListener) {
        Anchor contentLocationAnchor = new Anchor("#", contentData.getString("text"));
        contentLocationAnchor.setWidthFull();
        contentLocationAnchor.getStyle().set("display", "block");
        contentLocationAnchor.getElement().setAttribute("data-target-id", contentDataId);
        contentLocationAnchor.getElement().addEventListener("click", scrollClickListener)
                .addEventData("event.preventDefault()");
    }
}
