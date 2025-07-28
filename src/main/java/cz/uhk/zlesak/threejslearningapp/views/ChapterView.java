package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.ChapterSelectionCombobox;
import cz.uhk.zlesak.threejslearningapp.components.EditorJs;
import cz.uhk.zlesak.threejslearningapp.components.NavigationContentComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.data.SubChapterForComboBox;
import cz.uhk.zlesak.threejslearningapp.models.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ChapterView Class - Shows the requested chapter from URL parameter. Initializes all the necessary elements
 * to provide the user with the chapter content in intuitive way.
 */
@PageTitle("Kapitola")
@Route("chapter/:chapterId?")
@JavaScript("./js/scroll-to-element-data-id.js")
@Tag("chapter-view")
public class ChapterView extends Composite<VerticalLayout> implements IView {
    private String chapterId;
    private final ChapterController chapterController;

    /// Chapter elements that needs to be accessible around this class
    TextField searchInChapterTextField = new TextField();
    ChapterSelectionCombobox chapterSelectionComboBox = new ChapterSelectionCombobox();
    NavigationContentComponent navigationContentLayout = new NavigationContentComponent();
    ProgressBar progressBar = new ProgressBar();
    EditorJs editorjs = new EditorJs();
    Three renderer = new Three();
    H1 header = new H1();

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     */
    @Autowired
    public ChapterView(ChapterController chapterController) {
        this.chapterController = chapterController;

        /// Chapter page layout elements
        HorizontalLayout secondaryNavigationBar = new HorizontalLayout();
        HorizontalLayout chapterPageLayout = new HorizontalLayout();

        VerticalLayout chapterNavigation = new VerticalLayout();
        VerticalLayout chapterContent = new VerticalLayout();
        VerticalLayout chapterModel = new VerticalLayout();

        /// Scrollers for overflowing content elements + DIV for renderer
        Scroller chapterNavigationScroller = new Scroller(navigationContentLayout, Scroller.ScrollDirection.VERTICAL);
        Scroller chapterContentScroller = new Scroller(editorjs, Scroller.ScrollDirection.VERTICAL);
        Div modelDiv = new Div(progressBar, renderer);
        modelDiv.setId("modelDiv");

        /// Component layout (secondary navbar on top, then main content in three columns (chapter navigation, content, model)
        secondaryNavigationBar.setWidthFull();
        secondaryNavigationBar.addClassName(Gap.MEDIUM);
        secondaryNavigationBar.setAlignItems(FlexComponent.Alignment.CENTER);
        secondaryNavigationBar.setFlexGrow(0, chapterSelectionComboBox);
        secondaryNavigationBar.setFlexGrow(1, header);
        secondaryNavigationBar.setFlexGrow(0, searchInChapterTextField);

        chapterPageLayout.setWidthFull();
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setClassName("chapterPageLayout");
        chapterPageLayout.setFlexGrow(0, chapterNavigation);
        chapterPageLayout.setFlexGrow(1, chapterContent);
        chapterPageLayout.setFlexGrow(1, chapterModel);

        chapterNavigation.addClassName(Gap.MEDIUM);
        chapterNavigation.setMinWidth("10vw");
        chapterNavigation.setWidth("min-content");
        chapterNavigation.setPadding(false);

        chapterContent.setHeightFull();
        chapterContent.getStyle().set("flex-grow", "1");
        chapterContent.setFlexGrow(1, chapterContentScroller);
        chapterContent.setPadding(false);

        chapterModel.setHeightFull();
        chapterModel.setWidthFull();
        chapterModel.addClassName(Gap.MEDIUM);
        chapterModel.setPadding(false);

        navigationContentLayout.setPadding(false);
        navigationContentLayout.setSpacing(false);
        navigationContentLayout.getThemeList().add("spacing-s");

        /// Elements size requirements on the layout
        chapterNavigationScroller.setMaxHeight("85vh");
        chapterContentScroller.setMaxHeight("85vh");
        modelDiv.setMaxHeight("85vh");
        modelDiv.setWidthFull();
        modelDiv.setHeight("85vh");
        renderer.getStyle().set("width", "100%");
        searchInChapterTextField.setPlaceholder("Vyhledat v textu kapitoly (NEIMPLEMENTOVÁNO)");
        searchInChapterTextField.setMinWidth("450px");
        chapterSelectionComboBox.setMinWidth("10vw");

        /// Layout assembly
        secondaryNavigationBar.add(chapterSelectionComboBox, header, searchInChapterTextField);
        chapterPageLayout.add(chapterNavigation, chapterContent, chapterModel);

        chapterNavigation.add(chapterNavigationScroller);
        chapterContent.add(chapterContentScroller);
        chapterModel.add(modelDiv);

        getContent().add(secondaryNavigationBar, chapterPageLayout);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }

    /**
     * Overridden beforeLeave function to proper disposal of the model renderer to free the used RAM memory immediately.
     *
     * @param event BeforeLeave
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }

    /**
     * ClientCallable function to hide progress abr after the model is properly loaded to the model renderer.
     */
    @ClientCallable
    public void hideProgressBar() {
        progressBar.setVisible(false);
    }

    /**
     * ClientCallable function to update progress bar to inform user about the current model loading state.
     *
     * @param progress Represents model loading progress in percentage
     */
    @ClientCallable
    public void updateProgress(double progress) {
        progressBar.setValue(progress);
        if (progress >= 1.0) {
            hideProgressBar();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()){
            event.forwardTo(ChapterListView.class);
        }
        chapterId = event.getRouteParameters().get("chapterId").orElse(null);
        if (chapterId == null) {
            Notification.show("Nelze načíst kapitolu bez ID", 3000, Notification.Position.MIDDLE);
            logger.error("Nelze načíst kapitolu bez ID");
            UI.getCurrent().navigate(ChapterListView.class);
        }

        try {
            chapterController.getChapter(chapterId);
            header.setText(chapterController.getChapterName());
            ModelEntity modelFile = chapterController.getChapterModel();
            try{
                String base64Model =  modelFile.getBase64File();
                renderer.loadModel(base64Model);
            }catch (Exception e){
                logger.error(e.getMessage());
                Notification.show("Nepovedlo se načíst model: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                throw e;
            }
            progressBar = new ProgressBar();
            List<SubChapterForComboBox> subChapterNames = chapterController.getSubChaptersNames();
            chapterSelectionComboBox.setItems(subChapterNames);
            chapterSelectionComboBox.setItemLabelGenerator(SubChapterForComboBox::text);
            if(!subChapterNames.isEmpty()) {
                chapterSelectionComboBox.setValue(subChapterNames.getFirst());
            }
            editorjs.setChapterContentData(chapterController.getSelectedSubChapterContent(chapterSelectionComboBox.getValue().id()));
            editorjs.toggleReadOnlyMode(true);

            navigationContentLayout.initializeSubChapterData(chapterController.getSubChaptersContent());
        } catch (Exception e) {
            Notification.show("Chyba při načítání kapitoly: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            logger.error("Chyba při načítání kapitoly", e);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }
}
