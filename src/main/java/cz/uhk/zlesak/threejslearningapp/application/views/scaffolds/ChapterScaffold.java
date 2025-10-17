package cz.uhk.zlesak.threejslearningapp.application.views.scaffolds;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.application.components.*;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.MarkdownEditorComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.scrollers.ChapterContentScroller;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.utils.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

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
    protected final NavigationContentComponent navigationContentLayout = new NavigationContentComponent();
    protected final EditorJsComponent editorjs = new EditorJsComponent();
    protected final NameTextField nameTextField = new NameTextField("Název kapitoly");
    protected final VerticalLayout chapterNavigation, chapterContent, chapterModel;
    protected final CustomI18NProvider i18NProvider;
    protected final ModelDiv modelDiv = new ModelDiv();
    protected final MarkdownEditor mdEditor = new MarkdownEditorComponent();

    /**
     * Constructor for ChapterScaffold.
     * Initializes the layout and components based on the specified view type.
     *
     */
    public ChapterScaffold() {
        this.i18NProvider = SpringContextUtils.getBean(CustomI18NProvider.class);

        //Main page layout
        HorizontalLayout chapterPageLayout = new HorizontalLayout();
        chapterNavigation = new VerticalLayout();
        chapterContent = new VerticalLayout();
        chapterModel = new VerticalLayout();

        SplitLayout splitLayout = new SplitLayout(chapterContent, chapterModel);

        chapterPageLayout.add(chapterNavigation, splitLayout);
        chapterPageLayout.setSizeFull();
        chapterPageLayout.setClassName("chapterPageLayout");
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setFlexGrow(0, chapterNavigation);
        chapterPageLayout.setFlexGrow(1, splitLayout);
        chapterPageLayout.getStyle().set("min-width", "0");
        chapterPageLayout.getStyle().set("min-height", "0");

        //Navigation layout
        Scroller chapterNavigationScroller = new Scroller(navigationContentLayout, Scroller.ScrollDirection.VERTICAL);
        navigationContentLayout.setPadding(false);
        navigationContentLayout.setSpacing(false);
        navigationContentLayout.getThemeList().add("spacing-s");
        chapterNavigationScroller.setSizeFull();
        chapterNavigation.add(chapterSelect, chapterNavigationScroller, searchTextField);
        chapterNavigation.addClassName(Gap.MEDIUM);
        chapterNavigation.setMinWidth("12vw");
        chapterNavigation.setWidth("min-content");
        chapterNavigation.setPadding(false);

        //Content layout
        Scroller chapterContentScroller = new ChapterContentScroller(editorjs, mdEditor);
        Scroller modelsScroller = new Scroller();


        TabSheet tabs = new TabSheet();
        Tab tabContent = new Tab(VaadinIcon.TEXT_INPUT.create(), new Span("Obsah"));
        Tab tabModels = new Tab(VaadinIcon.CHART_3D.create(), new Span("3D Modely"));
        tabs.add(tabContent, chapterContentScroller);
        tabs.add(tabModels, modelsScroller);
        tabs.setPrefixComponent(nameTextField);

        Scroller tabsScroller = new Scroller(tabs, Scroller.ScrollDirection.VERTICAL);
        tabsScroller.setSizeFull();

        chapterContent.add(tabsScroller);
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
}
