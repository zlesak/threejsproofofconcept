package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.dom.DomEventListener;
import elemental.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("prototype")
public class LocatorAnchorComponent extends Anchor {
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
