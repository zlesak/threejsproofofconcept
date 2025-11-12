package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.containers.ChapterTabSheetContainer;
import cz.uhk.zlesak.threejslearningapp.components.scrollers.ModelsSelectScroller;
import cz.uhk.zlesak.threejslearningapp.components.containers.ChapterNavigationContainer;
import cz.uhk.zlesak.threejslearningapp.components.containers.ModelContainer;
import cz.uhk.zlesak.threejslearningapp.components.editors.EditorJs;
import cz.uhk.zlesak.threejslearningapp.components.editors.MarkdownEditor;
import cz.uhk.zlesak.threejslearningapp.components.scrollers.ChapterContentScroller;
import cz.uhk.zlesak.threejslearningapp.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.components.inputs.textFields.NameTextField;
import cz.uhk.zlesak.threejslearningapp.components.inputs.textFields.SearchTextField;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.IView;
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
public abstract class ChapterLayout extends Composite<VerticalLayout> implements IView {
    protected final SearchTextField searchTextField = new SearchTextField("Hledat v kapitole");
    protected final ChapterSelect chapterSelect = new ChapterSelect();
    protected final ChapterNavigationContainer navigationContentLayout = new ChapterNavigationContainer(chapterSelect, searchTextField);
    protected final MarkdownEditor mdEditor = new MarkdownEditor();
    protected final EditorJs editorjs = new EditorJs();
    protected final NameTextField nameTextField = new NameTextField("Název kapitoly");
    protected final VerticalLayout chapterContent, chapterModel;
    protected final ModelContainer modelDiv = new ModelContainer();
    protected ChapterTabSheetContainer secondaryNavigation = null;

    /**
     * Constructor for ChapterScaffold.
     * Initializes the layout and components based on the specified view type.
     *
     */
    public ChapterLayout(boolean createChapterMode) {
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

        ChapterContentScroller chapterContentScroller = new ChapterContentScroller(editorjs, mdEditor);
        ModelsSelectScroller modelsScroller = new ModelsSelectScroller();

        if (createChapterMode) {
            secondaryNavigation = new ChapterTabSheetContainer(nameTextField, chapterContentScroller, modelsScroller);
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

        searchTextField.addValueChangeListener(
                event -> editorjs.search(event.getValue())
        );
    }

    /**
     * Sets up the model div with event listeners and initializes texture selects.
     * @param quickModelEntityMap a map of model IDs to QuickModelEntity objects used for initialization
     */
    protected void setupModelDiv(Map<String, QuickModelEntity> quickModelEntityMap) {
        editorjs.addModelTextureColorAreaClickListener((modelId, textureId, hexColor, text) -> {
            modelDiv.modelTextureAreaSelectContainer.getModelListingSelect().setSelectedModelById(modelId);
            modelDiv.modelTextureAreaSelectContainer.getTextureListingSelect().setSelectedTextureById(textureId);
            modelDiv.modelTextureAreaSelectContainer.getTextureAreaSelect().setSelectedAreaByHexColor(hexColor, textureId);
        });
        editorjs.initializeTextureSelects(quickModelEntityMap);
        modelDiv.modelTextureAreaSelectContainer.initializeData(quickModelEntityMap);
    }
}
