package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.ChapterSelectionCombobox;
import cz.uhk.zlesak.threejslearningapp.components.EditorJsComponent;
import cz.uhk.zlesak.threejslearningapp.components.NavigationContentComponent;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

/**
 * ChapterScaffold - základní layout pro kapitoly, znovupoužitelný pro různé situce (create, update, read).
 */
@Slf4j
@Tag("chapter-scaffold")
@JavaScript("./js/scroll-to-element-data-id.js")
@Scope("prototype")
public abstract class ChapterScaffold extends Composite<VerticalLayout> implements IView {
    protected final TextField searchInChapterTextField = new TextField();
    protected final ChapterSelectionCombobox chapterSelectionComboBox = new ChapterSelectionCombobox();
    protected final NavigationContentComponent navigationContentLayout = new NavigationContentComponent();
    protected final ProgressBar progressBar = new ProgressBar();
    protected final EditorJsComponent editorjs = new EditorJsComponent();
    protected final ThreeJsComponent renderer = new ThreeJsComponent();
    protected final TextField chapterNameTextField = new TextField();
    protected final Div modelDiv = new Div(progressBar, renderer);
    protected final HorizontalLayout secondaryNavigationBar = new HorizontalLayout();

    protected final VerticalLayout chapterContent = new VerticalLayout();
    protected final VerticalLayout chapterModel = new VerticalLayout();

    public ChapterScaffold(ViewTypeEnum viewType) {
        // Main layout components
        HorizontalLayout chapterPageLayout = new HorizontalLayout();
        VerticalLayout chapterNavigation = new VerticalLayout();

        Scroller chapterNavigationScroller = new Scroller(navigationContentLayout, Scroller.ScrollDirection.VERTICAL);
        Scroller chapterContentScroller = new Scroller(editorjs, Scroller.ScrollDirection.VERTICAL);

        // Sestavení layoutu
        secondaryNavigationBar.add(chapterSelectionComboBox, chapterNameTextField, searchInChapterTextField);
        chapterPageLayout.add(chapterNavigation, chapterContent, chapterModel);
        chapterNavigation.add(chapterNavigationScroller);
        chapterContent.add(chapterContentScroller);
        chapterModel.add(modelDiv);

        switch (viewType) {
            case CREATE -> {
                chapterSelectionComboBox.setVisible(false);
                searchInChapterTextField.setVisible(false);
                chapterNameTextField.setPlaceholder("Název kapitoly");
                chapterNavigation.setVisible(false);
                editorjs.toggleReadOnlyMode(false);
                showProgressBar(false);
                showRenderer(false);

            }
            case EDIT -> {

            }
            case VIEW -> editorjs.toggleReadOnlyMode(true);
        }

//nastavení samostatných elementů
        //Nastavení divId, DŮLEŽITÉ PRO THREEJS
        modelDiv.setId("modelDiv");

        chapterNameTextField.setMaxLength(255);
        chapterNameTextField.setRequired(true);
        chapterNameTextField.setRequiredIndicatorVisible(true);
        chapterNameTextField.setWidthFull();

        // Nastavení vzhledu layoutu
        secondaryNavigationBar.setWidthFull();
        secondaryNavigationBar.addClassName(Gap.MEDIUM);
        secondaryNavigationBar.setAlignItems(FlexComponent.Alignment.CENTER);
        secondaryNavigationBar.setFlexGrow(0, chapterSelectionComboBox);
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

        modelDiv.setWidthFull();
        modelDiv.setHeightFull();
        renderer.getStyle().set("width", "100%");
        searchInChapterTextField.setPlaceholder("Vyhledat v textu kapitoly (NEIMPLEMENTOVÁNO)");
        searchInChapterTextField.setMinWidth("450px");
        chapterSelectionComboBox.setMinWidth("10vw");

        progressBar.setIndeterminate(true);

        getContent().add(secondaryNavigationBar, chapterPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");

        chapterSelectionComboBox.addValueChangeListener(event -> {
            var oldSelectedSubchapter = event.getOldValue();
            var newSelectedSubchapter = event.getValue();
            if (oldSelectedSubchapter != null) {
                navigationContentLayout.hideSubchapterNavigationContent(oldSelectedSubchapter.id());
            }
            if (newSelectedSubchapter != null) {
                navigationContentLayout.showSubchapterNavigationContent(newSelectedSubchapter.id());
            }
        });

        renderer.addModelLoadedEventListener(event -> {
            showProgressBar(false);
            showRenderer(true);
        });
    }

    public void showRenderer(Boolean show) {
        renderer.setVisible(show);
    }

    public void showProgressBar(Boolean show) {
        progressBar.setVisible(show);
    }
}
