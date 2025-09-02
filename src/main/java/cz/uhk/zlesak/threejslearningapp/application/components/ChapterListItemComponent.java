package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.ChapterEntity;
import org.jetbrains.annotations.NotNull;

@Tag("div")
public class ChapterListItemComponent extends Div {

    public ChapterListItemComponent(ChapterEntity chapter) {
        setWidthFull();
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "8px");
        getStyle().set("padding", "6px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        Span modelName = new Span(chapter.getName());
        Button openButton = getOpenButton(chapter);

        layout.add(modelName, openButton);
        add(layout);
    }

    @NotNull
    private static Button getOpenButton(ChapterEntity chapter) {
        Button button = new Button("Otevřít");
        button.getStyle().set("margin-left", "auto");
        button.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("chapterEntity", chapter);
            UI.getCurrent().navigate("chapter/" + chapter.getId());
        });
        return button;
    }
}
