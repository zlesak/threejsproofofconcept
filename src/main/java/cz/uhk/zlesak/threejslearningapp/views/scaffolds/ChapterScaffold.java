package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.EditorJsComponent;
import cz.uhk.zlesak.threejslearningapp.components.ModelDiv;
import cz.uhk.zlesak.threejslearningapp.components.NavigationContentComponent;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
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
    protected final TextField searchInChapterTextField = new TextField();
    protected final ChapterSelect chapterSelect = new ChapterSelect();
    protected final NavigationContentComponent navigationContentLayout = new NavigationContentComponent();
    protected final EditorJsComponent editorjs = new EditorJsComponent();
    protected final ThreeJsComponent renderer = new ThreeJsComponent();
    protected final TextField chapterNameTextField = new TextField();
    protected final ModelDiv modelDiv = new ModelDiv(renderer);
    protected final HorizontalLayout secondaryNavigationBar;

    protected final VerticalLayout chapterContent = new VerticalLayout();
    protected final VerticalLayout chapterModel = new VerticalLayout();
    protected final CustomI18NProvider i18NProvider;

    /**
     * Constructor for ChapterScaffold.
     * Initializes the layout and components based on the specified view type.
     *
     * @param viewType the type of view (CREATE, EDIT, VIEW) determining the layout and component behavior. (TODO move away)
     */
    public ChapterScaffold(ViewTypeEnum viewType) {
        this.i18NProvider = SpringContextUtils.getBean(CustomI18NProvider.class);
        HorizontalLayout chapterPageLayout = new HorizontalLayout();
        VerticalLayout chapterNavigation = new VerticalLayout();

        Scroller chapterNavigationScroller = new Scroller(navigationContentLayout, Scroller.ScrollDirection.VERTICAL);
        Scroller chapterContentScroller = new Scroller(editorjs, Scroller.ScrollDirection.VERTICAL);

        secondaryNavigationBar = new HorizontalLayout();
        Div firstDivider = new Div(new HorizontalLayout(chapterSelect, chapterNameTextField, searchInChapterTextField));
        firstDivider.setWidthFull();
        secondaryNavigationBar.add(firstDivider);

        chapterPageLayout.add(chapterNavigation, chapterContent, chapterModel);
        chapterNavigation.add(chapterNavigationScroller);
        chapterContent.add(chapterContentScroller);
        chapterModel.add(modelDiv);

        switch (viewType) {
            case CREATE -> {
                chapterSelect.setVisible(false);
                searchInChapterTextField.setVisible(false);
                chapterNameTextField.setPlaceholder("Název kapitoly");
                chapterNavigation.setVisible(false);
                editorjs.toggleReadOnlyMode(false);
            }
            case EDIT -> {

            }
            case VIEW -> editorjs.toggleReadOnlyMode(true);
        }

        chapterNameTextField.setMaxLength(255);
        chapterNameTextField.setRequired(true);
        chapterNameTextField.setRequiredIndicatorVisible(true);
        chapterNameTextField.setWidthFull();

        secondaryNavigationBar.setWidthFull();
        secondaryNavigationBar.addClassName(Gap.MEDIUM);
        secondaryNavigationBar.setAlignItems(FlexComponent.Alignment.CENTER);
        secondaryNavigationBar.setFlexGrow(0, chapterSelect);
        secondaryNavigationBar.setFlexGrow(1, chapterNameTextField);
        secondaryNavigationBar.setFlexGrow(0, searchInChapterTextField);

        chapterPageLayout.setWidthFull();
        chapterPageLayout.setClassName("chapterPageLayout");
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setFlexGrow(0, chapterNavigation);
        chapterPageLayout.setFlexGrow(1, chapterContent);
        chapterPageLayout.setFlexGrow(1, chapterModel);
        chapterPageLayout.getStyle().set("min-width", "0");
        chapterPageLayout.getStyle().set("min-height", "0");
        chapterPageLayout.setHeightFull();

        chapterNavigation.addClassName(Gap.MEDIUM);
        chapterNavigation.setMinWidth("10vw");
        chapterNavigation.setWidth("min-content");
        chapterNavigation.setPadding(false);

        chapterContent.setHeightFull();
        chapterContent.getStyle().set("flex-grow", "1");
        chapterContent.setFlexGrow(1, chapterContentScroller);
        chapterContent.setPadding(false);
        chapterContent.getStyle().set("min-width", "0");

        chapterContentScroller.setWidthFull();
        chapterContentScroller.setHeightFull();

        chapterModel.setHeightFull();
        chapterModel.setWidthFull();
        chapterModel.addClassName(Gap.MEDIUM);
        chapterModel.setPadding(false);
        chapterModel.getStyle().set("min-width", "0");

        navigationContentLayout.setPadding(false);
        navigationContentLayout.setSpacing(false);
        navigationContentLayout.getThemeList().add("spacing-s");

        renderer.getStyle().set("width", "100%");
        searchInChapterTextField.setPlaceholder("Vyhledat v textu kapitoly (NEIMPLEMENTOVÁNO)");
        searchInChapterTextField.setMinWidth("150px");
        chapterSelect.setMinWidth("10vw");

        getContent().add(secondaryNavigationBar, chapterPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");

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
    }
}
