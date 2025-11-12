package cz.uhk.zlesak.threejslearningapp.components.lists;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Component representing a single item in a list of models.
 * Displays the model name and a button to open the model.
 * When the button is clicked, it navigates to the model's detail view.
 */
@Slf4j
@Tag("div")
public class ModelListItem extends Div implements I18nAware {
    private final Button selectButton;

    /**
     * Constructor for ModelListItemComponent.
     * Initializes the component with the model's name and an "Open" button.
     *
     * @param model the QuickModelEntity representing the model to be displayed in the list item
     */
    public ModelListItem(QuickModelEntity model, boolean listView) {
        setWidthFull();
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "8px");

        VerticalLayout details = new VerticalLayout();

        HorizontalLayout modelName = new HorizontalLayout();
        Span nameLabel = new Span(text("model.title") + ": ");
        Span name = new Span(model.getModel().getName());
        name.getStyle().set("font-weight", "600");
        modelName.add(nameLabel, name);
        details.add(modelName);

        if (model.getMainTexture() != null) {
            HorizontalLayout creatorRow = new HorizontalLayout();
            creatorRow.add(new Span(text("model.mainTexture") + ": "), new Span(model.getMainTexture().getName()));
            details.add(creatorRow);
        }
        if (model.getOtherTextures() != null && !model.getOtherTextures().isEmpty()) {
            HorizontalLayout modelsRow = new HorizontalLayout();
            modelsRow.setAlignItems(HorizontalLayout.Alignment.CENTER);
            modelsRow.add(new Span(text("model.otherTextures") + ": "));
            List<String> otherTexturesNames = model.getOtherTextures().stream()
                    .filter(texture -> texture != null && texture.getName() != null)
                    .map(QuickTextureEntity::getName)
                    .toList();
            modelsRow.add(new Span(String.join(", ", otherTexturesNames)));
            details.add(modelsRow);
        }

        Button openButton = getOpenButton(model, listView, text("button.open"), text("openModelInNewTabButton.label"));
        selectButton = getSeletButton(listView);

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        row.setAlignItems(HorizontalLayout.Alignment.CENTER);
        row.add(details, openButton, selectButton);

        add(row);
    }

    @NotNull
    private Button getSeletButton(boolean listView) {
        Button selectButton = new Button(text("button.select"));
        selectButton.getStyle().set("margin", "12px").set("padding", "8px 24px");
        selectButton.setVisible(!listView);
        return selectButton;
    }

    /**
     * Creates and returns the "Open" button for the model.
     * The button's behavior changes based on whether the component is in a list view or not.
     *
     * @param model    the QuickModelEntity representing the model
     * @param listView boolean indicating if the component is in a list view
     * @return the configured "Open" button
     */
    @NotNull
    private static Button getOpenButton(QuickModelEntity model, boolean listView, String label, String text) {
        Button button = new Button(label);
        button.getStyle().set("margin", "12px").set("padding", "8px 24px");

        if (!listView) {
            button.setText(text);
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
     *
     * @param listener the click event listener to be set on the select button
     */
    public void setSelectButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        selectButton.addClickListener(listener);
    }
}
