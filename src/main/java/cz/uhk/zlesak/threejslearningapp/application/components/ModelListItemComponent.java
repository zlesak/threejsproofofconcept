package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Component representing a single item in a list of models.
 * Displays the model name and a button to open the model.
 * When the button is clicked, it navigates to the model's detail view.
 */
@Slf4j
@Tag("div")
public class ModelListItemComponent extends Div {
    private final Button selectButton;

    /**
     * Constructor for ModelListItemComponent.
     * Initializes the component with the model's name and an "Open" button.
     *
     * @param model the QuickModelEntity representing the model to be displayed in the list item
     */
    public ModelListItemComponent(QuickModelEntity model, boolean listView) {
        setWidthFull();
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "8px");
        getStyle().set("padding", "6px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        Span modelName = new Span(model.getModel().getName());
        Button openButton = getOpenButton(model, listView);

        selectButton = new Button("Vybrat");
        selectButton.getStyle().set("margin-left", "8px");
        selectButton.setVisible(!listView);

        layout.add(modelName, openButton, selectButton);
        add(layout);
    }

    /**
     * Creates and returns the "Open" button for the model.
     * The button's behavior changes based on whether the component is in a list view or not.
     * @param model the QuickModelEntity representing the model
     * @param listView boolean indicating if the component is in a list view
     * @return the configured "Open" button
     */
    @NotNull
    private static Button getOpenButton(QuickModelEntity model, boolean listView) {
        Button button = new Button("Otevřít");
        button.getStyle().set("margin-left", "auto");

        if (!listView) {
            button.setText("Prohlédnout v novém okně");
        }
        button.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("quickModelEntity", model);
            if (listView) {
                UI.getCurrent().navigate("model/" + model.getModel().getId());
            } else {
                UI.getCurrent().getPage().executeJs("window.open($0, '_blank')", "model/" + model.getModel().getId());
            }
        });
        return button;
    }

    /**
     * Sets a click listener for the select button.
     * This allows external components to define behavior when the select button is clicked.
     * @param listener the click event listener to be set on the select button
     */
    public void setSelectButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        selectButton.addClickListener(listener);
    }
}
