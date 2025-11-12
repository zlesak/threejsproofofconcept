package cz.uhk.zlesak.threejslearningapp.events.threejs;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import lombok.Getter;

/**
 * Event fired when search is performed via the click of the button in FilterComponent.
 */
@Getter
public class ThreeJsSearchEvent extends ComponentEvent<UI> {
    private final String value;
    private final SortDirectionEnum sortDirection;
    private final String orderBy;

    public ThreeJsSearchEvent(String value, SortDirectionEnum sortDirection, String orderBy, UI source) {
        super(source, false);
        this.value = value;
        this.sortDirection = sortDirection;
        this.orderBy = orderBy;
    }
}
