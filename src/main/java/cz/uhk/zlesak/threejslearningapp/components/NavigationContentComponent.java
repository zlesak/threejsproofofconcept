package cz.uhk.zlesak.threejslearningapp.components;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.DomEventListener;

import static cz.uhk.zlesak.threejslearningapp.controllers.ChapterController.getAnchor;

public class NavigationContentComponent extends VerticalLayout {

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
                Anchor mainHeadingAnchor = getAnchor(h1.getObject("data"), h1.getString("id"), scrollClickListener);
                contentLayout.add(mainHeadingAnchor);

                for (int j = 0; j < content.length(); j++) {
                    JsonObject contentBlock = content.getObject(j);
                    JsonObject contentData = contentBlock.getObject("data");
                    String contentDataId = contentBlock.getString("id");
                    Anchor contentLocationAnchor = getAnchor(contentData, contentDataId, scrollClickListener);
                    contentLocationAnchor.setId(contentDataId);
                    contentLayout.add(contentLocationAnchor);
                }
                hideSubchapterNavigationContent(h1.getString("id"));
                this.add(contentLayout);
            }
        }
    }

    public void hideSubchapterNavigationContent(String subchapterId) {
        UI.getCurrent().getPage().executeJs(
                "const el = document.getElementById($0); if (el) { el.style.display = 'none'; }",
                subchapterId
        );
    }
    public void showSubchapterNavigationContent(String subchapterId) {
        UI.getCurrent().getPage().executeJs(
                "const el = document.getElementById($0); if (el) { el.style.display = 'block'; }",
                subchapterId
        );
    }
}
