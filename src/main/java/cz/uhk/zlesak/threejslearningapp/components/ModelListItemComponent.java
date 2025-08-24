package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * Component representing a single item in a list of models.
 * Displays the model name and a button to open the model.
 * When the button is clicked, it navigates to the model's detail view.
 */
@Slf4j
@Tag("div")
public class ModelListItemComponent extends Div {

    /**
     * Constructor for ModelListItemComponent.
     * Initializes the component with the model's name and an "Open" button.
     * @param model the QuickModelEntity representing the model to be displayed in the list item
     */
    public ModelListItemComponent(QuickModelEntity model) {
        setWidthFull();
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "8px");
        getStyle().set("padding", "6px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        Span modelName = new Span(model.getModel().getName());
        Button button = new Button("Otevřít");
        button.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("quickModelEntity", model);
            UI.getCurrent().navigate("model/" + model.getModel().getId());
        });
        button.getStyle().set("margin-left", "auto");

        layout.add(modelName, button);
        add(layout);
    }
}
