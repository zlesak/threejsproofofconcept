package cz.uhk.zlesak.threejslearningapp.application.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.application.components.*;
import cz.uhk.zlesak.threejslearningapp.application.components.divs.ModelDiv;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.MarkdownEditorComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.scrollers.ChapterContentScroller;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.application.components.textFields.NameTextField;
import cz.uhk.zlesak.threejslearningapp.application.components.textFields.SearchTextField;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * ChapterScaffold is an abstract base class for views related to chapter management, including creating, editing, and viewing chapters.
 * It provides a common layout and components such as navigation, content editor, and 3D model display.
 * The layout is responsive and adjusts based on the view type (create, edit, view).
 * It includes a secondary navigation bar with chapter selection and search functionality.
 * The class is designed to be extended by specific chapter-related views.
 */
@Slf4j
@Tag("chapter-scaffold")
@JavaScript("./js/scroll-to-element-data-id.js")
@Scope("prototype")
public abstract class ChapterScaffold extends Composite<VerticalLayout> implements IView {
    protected final SearchTextField searchTextField = new SearchTextField("Hledat v kapitole");
    protected final ChapterSelect chapterSelect = new ChapterSelect();
    protected final NavigationContentComponent navigationContentLayout = new NavigationContentComponent(chapterSelect, searchTextField);
    protected final MarkdownEditorComponent mdEditor = new MarkdownEditorComponent();
    protected final EditorJsComponent editorjs = new EditorJsComponent();
    protected final NameTextField nameTextField = new NameTextField("Název kapitoly");
    protected final VerticalLayout chapterContent, chapterModel;
    protected final ModelDiv modelDiv = new ModelDiv();
    protected ChapterTabSheetSecondaryNavigationComponent secondaryNavigation = null;

    /**
     * Constructor for ChapterScaffold.
     * Initializes the layout and components based on the specified view type.
     *
     */
    public ChapterScaffold(boolean areWeInCreateOrEditChapterView) {

        //Main page layout
        HorizontalLayout chapterPageLayout = new HorizontalLayout();
        chapterContent = new VerticalLayout();
        chapterModel = new VerticalLayout();

        SplitLayout splitLayout = new SplitLayout(chapterContent, chapterModel);

        chapterPageLayout.add(navigationContentLayout, splitLayout);
        chapterPageLayout.setSizeFull();
        chapterPageLayout.setClassName("chapterPageLayout");
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setFlexGrow(0, navigationContentLayout);
        chapterPageLayout.setFlexGrow(1, splitLayout);
        chapterPageLayout.getStyle().set("min-width", "0");
        chapterPageLayout.getStyle().set("min-height", "0");


        //Content layout

        ChapterContentScroller chapterContentScroller = new ChapterContentScroller(editorjs, mdEditor);
        ModelsSelectScrollerComponent modelsScroller = new ModelsSelectScrollerComponent();

        if (areWeInCreateOrEditChapterView) {
            secondaryNavigation = new ChapterTabSheetSecondaryNavigationComponent(nameTextField, chapterContentScroller, modelsScroller);
            Scroller tabsScroller = new Scroller(secondaryNavigation, Scroller.ScrollDirection.VERTICAL);
            tabsScroller.setSizeFull();
            chapterContent.add(tabsScroller);
        } else {
            nameTextField.setWidthFull();
            chapterContent.add(nameTextField, chapterContentScroller);
        }
        chapterContent.addClassName(Gap.MEDIUM);
        chapterContent.setSizeFull();
        chapterContent.setPadding(false);

        //Model layout
        chapterModel.add(modelDiv);
        chapterModel.addClassName(Gap.MEDIUM);
        chapterModel.setSizeFull();
        chapterModel.setPadding(false);
        chapterModel.getStyle().set("min-width", "0");
        chapterModel.getStyle().set("flex-grow", "1");

        getContent().add(chapterPageLayout);
        getContent().setSizeFull();

        chapterSelect.addValueChangeListener(event -> {
            var oldSelectedSubchapter = event.getOldValue();
            var newSelectedSubchapter = event.getValue();
            if (oldSelectedSubchapter != null) {
                navigationContentLayout.hideSubchapterNavigationContent(oldSelectedSubchapter.id());
            }
            if (newSelectedSubchapter != null) {
                navigationContentLayout.showSubchapterNavigationContent(newSelectedSubchapter.id());
            }
        });
        searchTextField.addValueChangeListener(
                event -> editorjs.search(event.getValue())
        );
    }

    protected void setupModelDiv(Map<String, QuickModelEntity> quickModelEntityMap) {
        editorjs.addModelTextureColorAreaClickListener((modelId, textureId, hexColor, text) -> {
            modelDiv.modelTextureAreaSelectComponent.getModelListingSelect().setSelectedModelById(modelId);
            modelDiv.modelTextureAreaSelectComponent.getTextureListingSelect().setSelectedTextureById(textureId);
            modelDiv.modelTextureAreaSelectComponent.getTextureAreaSelect().setSelectedAreaByHexColor(hexColor, textureId);
        });
        editorjs.initializeTextureSelects(quickModelEntityMap);
        modelDiv.modelTextureAreaSelectComponent.initializeData(quickModelEntityMap);
    }
}
