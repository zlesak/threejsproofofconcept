package cz.uhk.zlesak.threejslearningapp.events.threejs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import org.springframework.data.domain.Sort;

/**
 * Event fired when the user asks a listing to search or re-sort.
 *
 * <p>The source is a plain {@link Component}: the toolbar that fires it is being replaced, and no
 * listener ever looked at the source's type — they all read the three values below.
 */
@Getter
public class SearchEvent extends ComponentEvent<Component> {
    private final String value;
    private final Sort.Direction sortDirection;
    private final String orderBy;

    /**
     * @param value         Search text entered by the user.
     * @param sortDirection Requested sort direction.
     * @param orderBy       Field name to sort by.
     * @param source        The toolbar firing the event.
     */
    public SearchEvent(String value, Sort.Direction sortDirection, String orderBy, Component source) {
        super(source, false);
        this.value = value;
        this.sortDirection = sortDirection;
        this.orderBy = orderBy;
    }
}
