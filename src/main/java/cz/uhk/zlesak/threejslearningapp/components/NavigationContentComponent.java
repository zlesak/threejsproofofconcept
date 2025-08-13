package cz.uhk.zlesak.threejslearningapp.components;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DomEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

/**
 * Component for displaying navigation content based on sub-chapters.
 * It initializes sub-chapter data and provides methods to show or hide sub-chapter navigation content.
 * The component listens for click events on individual "catch" points that then scroll to specific sub-chapter locations.
 */
@Slf4j
@Scope("prototype")
public class NavigationContentComponent extends VerticalLayout {

    /**
     * Initializes the sub-chapter data by parsing the provided JSON content.
     * It creates a vertical layout for each sub-chapter and adds anchors for each content block.
     * Each anchor is set up with a click listener that scrolls to the corresponding sub-chapter location in EditorJs
     * @param subChaptersContent The JSON content containing sub-chapter data, including headings and content blocks.
     */
    public void initializeSubChapterData(JsonValue subChaptersContent) {
        DomEventListener scrollClickListener = event -> {
            String dataIdToScroll = event.getSource().getAttribute("data-target-id");
            UI.getCurrent().getPage().executeJs("window.scrollToDataId($0)", dataIdToScroll);
        };
        Span contentNavigationText = new Span("Podkapitoly");
        this.add(contentNavigationText);

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
                this.add(contentLayout);
            }
        }
    }

    /**
     * Hides the sub-chapter navigation by setting its display style to 'none'.
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
     * @param subchapterId The ID of the sub-chapter element to show.
     */
    public void showSubchapterNavigationContent(String subchapterId) {
        UI.getCurrent().getPage().executeJs(
                "const el = document.getElementById($0); if (el) { el.style.display = 'block'; }",
                subchapterId
        );
    }
}
