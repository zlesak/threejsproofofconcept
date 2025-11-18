package cz.uhk.zlesak.threejslearningapp.components.lists;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Tag("div")
public class ChapterListItem extends Div implements I18nAware {
    private final Button selectButton = new Button(text("button.select"));

    public ChapterListItem(ChapterEntity chapter, boolean forQuiz) {
        setWidthFull();
        getStyle().set("border", "1px solid #ccc");
        getStyle().set("border-radius", "8px");

        VerticalLayout details = new VerticalLayout();
        details.setWidth("50%");

        HorizontalLayout chapterName = new HorizontalLayout();
        Span nameLabel = new Span(text("chapter.title") + ": ");
        Span name = new Span(chapter.getName());
        name.getStyle().set("font-weight", "600");
        chapterName.add(nameLabel, name);
        details.add(chapterName);

        if (chapter.getCreatorId() != null && !chapter.getCreatorId().isBlank()) {
            HorizontalLayout creatorRow = new HorizontalLayout();
            creatorRow.add(new Span(text("chapter.creator") + ": "), new Span(chapter.getCreatorId()));
            details.add(creatorRow);
        }
        if (chapter.getCreated() != null) {
            HorizontalLayout dateRow = new HorizontalLayout();
            dateRow.add(new Span(text("chapter.creationDate") + ": "), new Span(DateTimeFormatter.ofPattern("d.M.yyyy HH:mm").withZone(ZoneId.systemDefault()).format(chapter.getCreated())));
            details.add(dateRow);
        }
        if (chapter.getUpdated() != null) {
            HorizontalLayout dateRow = new HorizontalLayout();
            dateRow.add(new Span(text("chapter.lastModified") + ": "), new Span(DateTimeFormatter.ofPattern("d.M.yyyy HH:mm").withZone(ZoneId.systemDefault()).format(chapter.getUpdated())));
            details.add(dateRow);
        }
        if (chapter.getModels() != null && !chapter.getModels().isEmpty()) {
            HorizontalLayout modelsRow = new HorizontalLayout();
            modelsRow.setAlignItems(HorizontalLayout.Alignment.CENTER);
            modelsRow.add(new Span(text("chapter.models") + ": "));
            for (QuickModelEntity model : chapter.getModels()) {
                if (model != null && model.getModel() != null) {
                    String modelName = model.getModel().getName();
                    String id = model.getModel().getId();
                    if (modelName != null && !modelName.isBlank() && id != null) {
                        Button modelButton = new Button(modelName);
                        modelButton.getStyle()
                                .set("background", "none")
                                .set("border", "none")
                                .set("color", "#1976d2")
                                .set("text-decoration", "underline")
                                .set("padding", "0")
                                .set("margin-right", "8px")
                                .set("cursor", "pointer");
                        modelButton.addClickListener(e -> {
                            VaadinSession.getCurrent().setAttribute("quickModelEntity", model);
                            UI.getCurrent().navigate("model/" + id);
                        });

                        modelsRow.add(modelButton);
                    }
                }
            }

            details.add(modelsRow);
        }
        HorizontalLayout row = new HorizontalLayout();

        if (!forQuiz) {
            Button openButton = getOpenButton(chapter, text("button.open"));

            Markdown markdown = new Markdown(text("chapter.content.loading"));
            markdown.setWidthFull();
            UI.getCurrent().getPage().executeJs(
                    "return window.convertEditorJsToMarkdown($0)", chapter.getContent()
            ).toCompletableFuture().whenComplete((md, t) -> markdown.setContent(md.asString()));
            row.add(details, markdown, openButton);
            row.setFlexGrow(0, details);
            row.setFlexGrow(1, markdown);
            row.setFlexGrow(0, openButton);// add select button to layout

        }
        else {
            selectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            selectButton.getStyle().set("margin", "12px").set("padding", "8px 24px");
            row.add(details, selectButton);
            row.setFlexGrow(0, details);
            row.setFlexGrow(0, selectButton);
        }

        row.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        row.setAlignItems(HorizontalLayout.Alignment.CENTER);


        add(row);
    }

    public void setSelectButtonClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        selectButton.addClickListener(listener);
    }

    @NotNull
    private static Button getOpenButton(ChapterEntity chapter, String label) {
        Button button = new Button(label);
        button.getStyle().set("margin", "12px").set("padding", "8px 24px");
        button.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("chapterEntity", chapter);
            UI.getCurrent().navigate("chapter/" + chapter.getId());
        });
        return button;
    }
}
