package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.*;
import cz.uhk.zlesak.threejslearningapp.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.utils.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.views.IView;
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
        chapterPageLayout.add(chapterNavigation, chapterContent, chapterModel);
        chapterPageLayout.setSizeFull();
        chapterPageLayout.setClassName("chapterPageLayout");
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setFlexGrow(0, chapterNavigation);
        chapterPageLayout.setFlexGrow(1, chapterContent);
        chapterPageLayout.setFlexGrow(1, chapterModel);
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
        Scroller chapterContentScroller = new Scroller(editorjs, Scroller.ScrollDirection.VERTICAL);
        chapterContentScroller.setSizeFull();
        chapterContent.add(nameTextField, chapterContentScroller);
        chapterContent.addClassName(Gap.MEDIUM);
        chapterContent.setFlexGrow(1, chapterContentScroller);
        chapterContent.setSizeFull();
        chapterContent.setPadding(false);
        chapterContent.getStyle().set("min-width", "0");
        chapterContent.getStyle().set("flex-grow", "1");

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
