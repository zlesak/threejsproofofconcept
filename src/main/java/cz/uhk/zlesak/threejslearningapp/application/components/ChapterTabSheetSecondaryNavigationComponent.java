package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.scrollers.ChapterContentScroller;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A TabSheet component for secondary navigation within a chapter.
 * It contains tabs for chapter content and 3D model selection.
 * Allows switching between content and model views.
 *
 */
@Slf4j
public class ChapterTabSheetSecondaryNavigationComponent extends TabSheet {

    @Getter
    private final ModelsSelectScrollerComponent modelsScroller;
    private final ChapterContentScroller chapterContentScroller;

    private final Tab tabModels = new Tab(VaadinIcon.CHART_3D.create(), new Span("3D Modely"));

    public ChapterTabSheetSecondaryNavigationComponent(NameTextField nameTextField, ChapterContentScroller chapterContentScroller, ModelsSelectScrollerComponent modelsScroller) {
        super();
        this.chapterContentScroller = chapterContentScroller;
        this.modelsScroller = modelsScroller;
        Tab tabContent = new Tab(VaadinIcon.FILE_TEXT.create(), new Span("Obsah"));
        add(tabContent, chapterContentScroller);
        add(tabModels, modelsScroller);
        setPrefixComponent(nameTextField);
        setSizeFull();

        setSelectedIndex(0);
    }

    public void setMainContentTabSelected() {
        setSelectedIndex(0);
    }

    public void init(EditorJsComponent editorJsComponent) {
        addSelectedChangeListener(event -> {
            if(event.getSelectedTab() == tabModels) {
                chapterContentScroller.setEnabled(true); //DO NOT REMOVE!! - could not get editorjs content normally when scroller is disabled

                editorJsComponent.getSubchaptersNames().whenComplete((subchapterNames, exception) -> {
                    if(exception != null) {
                        log.error("Error while getting subchapter names for model selects initialization", exception);
                        return;
                    }
                    modelsScroller.initSelects(subchapterNames);
                    chapterContentScroller.setEnabled(false);
                });
            }
        });
    }
}