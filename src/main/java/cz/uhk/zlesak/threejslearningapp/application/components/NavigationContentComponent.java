package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.application.components.textFields.SearchTextField;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

/**
 * Component for displaying navigation content based on sub-chapters.
 * It initializes sub-chapter data and provides methods to show or hide sub-chapter navigation content.
 * The component listens for click events on individual "catch" points that then scroll to specific sub-chapter locations.
 * Includes a toggle button to collapse/expand the navigation content.
 */
@Slf4j
@Scope("prototype")
public class NavigationContentComponent extends VerticalLayout {

    private final Button toggleButton;
    private final VerticalLayout contentContainer;
    private final VerticalLayout searchContainer;
    private boolean isExpanded = true;

    /**
     * Constructor for NavigationContentComponent.
     * Initializes the toggle button, scroller, and content container.
     */
    public NavigationContentComponent(ChapterSelect chapterSelect, SearchTextField searchTextField) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        addClassName(LumoUtility.Gap.MEDIUM);
        setMinWidth("12vw");
        setWidth("min-content");

        getStyle().set("overflow", "visible");

        toggleButton = new Button(VaadinIcon.CHEVRON_LEFT.create());
        toggleButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggleButton.setTooltipText("Skrýt navigaci");
        toggleButton.addClickListener(e -> toggleNavigation());

        contentContainer = new VerticalLayout();
        contentContainer.setPadding(false);
        contentContainer.setSpacing(false);
        contentContainer.getThemeList().add("spacing-s");

        Scroller contentScroller = new Scroller(contentContainer, Scroller.ScrollDirection.VERTICAL);
        contentScroller.setSizeFull();
        contentScroller.getStyle().set("flex-grow", "1");

        searchTextField.setWidthFull();

        toggleButton.getStyle()
                .set("width", "20px")
                .set("background-color", "rgba(233, 235, 239, 1)")
                .set("position", "absolute")
                .set("top", "calc(100% - 26px)")
                .set("right", "0")
                .set("transform", "translate(calc(100% + 8px), -50%)")
                .set("z-index", "100");

        searchContainer = new VerticalLayout(searchTextField, toggleButton);
        searchContainer.setPadding(false);
        searchContainer.setSpacing(false);
        searchContainer.setWidthFull();
        searchContainer.getStyle()
                .set("position", "relative")
                .set("overflow", "visible");

        add(chapterSelect, contentScroller, searchContainer);
    }

    /**
     * Toggles the visibility of the navigation content.
     */
    private void toggleNavigation() {
        isExpanded = !isExpanded;

        getChildren().forEach(component -> {
            if (component != searchContainer) {
                component.setVisible(isExpanded);
            }
        });

        searchContainer.getChildren().forEach(child -> {
            if (child != toggleButton) {
                child.setVisible(isExpanded);
            }
        });

        if (isExpanded) {
            toggleButton.setIcon(VaadinIcon.CHEVRON_LEFT.create());
            toggleButton.setTooltipText("Skrýt navigaci");
            setWidth("min-content");
            setMinWidth("12vw");
            toggleButton.getStyle()
                    .set("width", "20px")
                    .set("height", "36px")
                    .set("top", "calc(100% - 36px)")
                    .set("right", "-10px")
                    .set("transform", "translate(calc(100% + 8px), -50%)");

        } else {
            toggleButton.setIcon(VaadinIcon.CHEVRON_RIGHT.create());
            toggleButton.setTooltipText("Zobrazit navigaci");
            setWidth("auto");
            setMinWidth("0");
            toggleButton.getStyle()
                    .set("height", "60px")
                    .set("margin", "15px")
                    .set("width", "20px")
                    .set("top", "50vh")
                    .set("right", "20px");
        }
    }

    /**
     * Initializes the sub-chapter data by parsing the provided JSON content.
     * It creates a vertical layout for each sub-chapter and adds anchors for each content block.
     * Each anchor is set up with a click listener that scrolls to the corresponding sub-chapter location in EditorJs
     *
     * @param subChaptersContent The JSON content containing sub-chapter data, including headings and content blocks.
     */
    public void initializeSubChapterData(JsonValue subChaptersContent) {
        DomEventListener scrollClickListener = event -> {
            String dataIdToScroll = event.getSource().getAttribute("data-target-id");
            UI.getCurrent().getPage().executeJs("window.scrollToDataId($0)", dataIdToScroll);
        };

        if (subChaptersContent instanceof JsonArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JsonObject obj = jsonArray.getObject(i);
                JsonObject h1 = obj.getObject("h1");
                JsonArray content = obj.getArray("content");

                VerticalLayout contentLayout = new VerticalLayout();
                contentLayout.setPadding(false);
                contentLayout.setId(h1.getString("id"));
                Anchor mainHeadingAnchor = new LocatorAnchorComponent(h1.getObject("data"), h1.getString("id"), scrollClickListener);
                contentLayout.add(mainHeadingAnchor);

                for (int j = 0; j < content.length(); j++) {
                    JsonObject contentBlock = content.getObject(j);
                    JsonObject contentData = contentBlock.getObject("data");
                    String contentDataId = contentBlock.getString("id");
                    Anchor contentLocationAnchor = new LocatorAnchorComponent(contentData, contentDataId, scrollClickListener);
                    contentLocationAnchor.setId(contentDataId);
                    contentLayout.add(contentLocationAnchor);
                }
                hideSubchapterNavigationContent(h1.getString("id"));
                contentContainer.add(contentLayout);
            }
        }
    }

    /**
     * Hides the sub-chapter navigation by setting its display style to 'none'.
     *
     * @param subchapterId The ID of the sub-chapter element to hide.
     */
    public void hideSubchapterNavigationContent(String subchapterId) {
        UI.getCurrent().getPage().executeJs(
                "const el = document.getElementById($0); if (el) { el.style.display = 'none'; }",
                subchapterId
        );
    }

    /**
     * Shows the sub-chapter navigation by setting its display style to 'block'.
     * This method is used to make the sub-chapter navigation visible when needed.
     *
     * @param subchapterId The ID of the sub-chapter element to show.
     */
    public void showSubchapterNavigationContent(String subchapterId) {
        UI.getCurrent().getPage().executeJs(
                "const el = document.getElementById($0); if (el) { el.style.display = 'block'; }",
                subchapterId
        );
    }
}
