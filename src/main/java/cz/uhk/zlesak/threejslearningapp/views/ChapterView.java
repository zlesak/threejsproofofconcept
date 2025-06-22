package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.components.EditorJs;
import cz.uhk.zlesak.threejslearningapp.controlers.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.data.SubChapterForComboBox;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * ChapterView Class - Shows the requested chapter from URL parameter. Initializes all the necessary elements
 * to provide the user with the chapter content in intuitive way.
 */
@PageTitle("Kapitola")
@Route("chapter/:chapterId?")
@JavaScript("./js/scroll-to-element-data-id.js")
@Tag("chapter-view")
public class ChapterView extends Composite<VerticalLayout> implements HasUrlParameter<String>, BeforeLeaveObserver {
    private final ChapterApiClient chapterApiClient;

    /// Chapter elements that needs to be accessible around this class
    TextField searchInChapterTextField = new TextField();
    ComboBox<SubChapterForComboBox> chapterSelectionComboBox = new ComboBox<>();
    Accordion subChapterContentAccordion = new Accordion();
    ProgressBar progressBar = new ProgressBar();
    EditorJs editorjs = new EditorJs();
    Three renderer = new Three();
    H1 header = new H1();

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     * @param chapterApiClient ChapterApiClient that is able to communicating with backend API endpoints in means of chapter context
     */
    public ChapterView(ChapterApiClient chapterApiClient) {
        this.chapterApiClient = chapterApiClient;

        /// Chapter page layout elements
        HorizontalLayout secondaryNavigationBar = new HorizontalLayout();
        HorizontalLayout chapterPageLayout = new HorizontalLayout();

        VerticalLayout chapterNavigation = new VerticalLayout();
        VerticalLayout chapterContent = new VerticalLayout();
        VerticalLayout chapterModel = new VerticalLayout();

        /// Scrollers for overflowing content elements + DIV for renderer
        Scroller chapterNavigationScroller = new Scroller(subChapterContentAccordion, Scroller.ScrollDirection.VERTICAL);
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


        /// Elements size requirements ion the layout
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

        /// on change functions
        //TODO Implement logic for subchapter selection
        //        chapterSelectionComboBox.addValueChangeListener(event -> {
        //            SubChapterForComboBox newSelectedSubchapter = event.getValue();
        //
        //            if (newSelectedSubchapter != null) {
        //                Notification.show(String.valueOf(newSelectedSubchapter.text()));
        //            } else {
        //
        //            }
        //        });

        // TODO implement searchInChapterTextField.addValueChangeListener
    }
// TODO implement
//    private void setSubChapterDataAfterComboboxSelect() {
//
//    }

    /**
     * Function to initialize chapter selection ComboBox after chapter has been loaded.
     * Provides options to select which subchapter list of content is served to the end user.
     *
     * @param comboBox    ComboBox to set the content to
     * @param subChapters SubChapters data (has to be got in advance via EditorJs instance)
     */
    private void initializeChapterSelectionComboBox(ComboBox<SubChapterForComboBox> comboBox, JsonValue subChapters) {
        List<SubChapterForComboBox> subChapterList = new ArrayList<>();

        if (subChapters instanceof JsonArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JsonObject obj = jsonArray.getObject(i);
                String id = obj.getString("id");
                String text = obj.getString("text");
                subChapterList.add(new SubChapterForComboBox(id, text));
            }
        }

        comboBox.setItems(subChapterList);
        comboBox.setItemLabelGenerator(SubChapterForComboBox::text);

        if (!subChapterList.isEmpty()) {
            comboBox.setValue(subChapterList.getFirst());
        }
    }

    /**
     * Fucntion to initialize subchapter data accordion after chapter has been loaded.
     * Provides user the option of fast navigation between topics via links, that moves user in DOM to the selected topic.
     *
     * @param accordion          Accordion to set the subchapter data to
     * @param subChaptersContent Subchapter content that is places in the accordion (has to be got in advance via EditorJs instance)
     */
    private void initializeSubChapterData(Accordion accordion, JsonValue subChaptersContent) {
        DomEventListener scrollClickListener = event -> {
            String dataIdToScroll = event.getSource().getAttribute("data-target-id");
            UI.getCurrent().getPage().executeJs("window.scrollToDataId($0)", dataIdToScroll);
        };

        if (subChaptersContent instanceof JsonArray jsonArray) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JsonObject obj = jsonArray.getObject(i);
                JsonObject h1 = obj.get("h1");
                JsonArray content = obj.getArray("content");

                VerticalLayout contentLayout = new VerticalLayout();

                for (int j = 0; j < content.length(); j++) {
                    JsonObject contentBlock = content.getObject(j);
                    JsonObject contentData = contentBlock.getObject("data");
                    String contentDataId = contentBlock.getString("id");
                    Notification.show(contentDataId);
                    Anchor contentLocationAnchor = new Anchor("#", contentData.getString("text"));
                    contentLocationAnchor.getElement().setAttribute("data-target-id", contentDataId);
                    contentLocationAnchor.getElement().addEventListener("click", scrollClickListener)
                            .addEventData("event.preventDefault()");

                    contentLayout.add(contentLocationAnchor);
                }

                accordion.add(h1.getObject("data").getString("text"), contentLayout);
            }
        }
    }

    /**
     * Function to initialize the UI of the ChapterView with provided chapter data.
     *
     * @param chapter  ChapterEntity data class with chapter to show to the end user
     * @param resource Resource, in this case 3D model, that is sent to ThreeJs model renderer
     */
    private void initializeUIWithChapterData(ChapterEntity chapter, Resource resource) {
        header.setText(chapter.getHeader());
        String content = chapter.getContent();

        editorjs.setData(content)
                .thenRun(() -> {
                    editorjs.getSubChaptersNames().whenComplete((subChapters, error) -> {
                        if (error != null) {
                            Notification.show("Chyba při získávání podkapitol: " + error.getMessage());
                        } else {
                            initializeChapterSelectionComboBox(chapterSelectionComboBox, subChapters);
                            editorjs.toggleReadOnlyMode(true);
                        }
                    });
                    editorjs.getSubchaptersContent().whenComplete((subchapterContent, error) -> {
                        if (error != null) {
                            Notification.show("Chyba při získávání obsahu podkapitol: " + error.getMessage());
                        } else {
                            initializeSubChapterData(subChapterContentAccordion, subchapterContent);
                        }
                    });
                })
                .exceptionally(error -> {
                    Notification.show("Error loading content: " + error.getMessage());
                    return null;
                });

        if (resource != null) {
            try {
                InputStream inputStream = resource.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                inputStream.close();

                String base64Model = Base64.getEncoder().encodeToString(bytes);

                renderer.getElement().executeJs("window.loadModel($0)",
                        "data:application/octet-stream;base64," + base64Model);

            } catch (IOException e) {
                Notification.show("Error loading model: " + e.getMessage(),
                        5000, Notification.Position.BOTTOM_CENTER);
            }
        }
    }

    /**
     * Function to load chapter requested via URL parameter. Calls all the necessary initialization functions after
     * the chapter is returned from the ChapterApiClient
     *
     * @param chapterId ChapterId that has been set in URL parameter
     */
    private void loadChapterData(String chapterId) {
        progressBar.setVisible(true);
        progressBar.setValue(0);
        try {
            ChapterEntity chapter = chapterApiClient.getChapter(chapterId);
            if (chapter != null) {
                try {
                    Resource resource = chapterApiClient.downloadModel(chapterId);
                    initializeUIWithChapterData(chapter, resource);
                    progressBar.setValue(1.0);
                } catch (Exception e) {
                    Notification.show("Error loading model: " + e.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
                }
            }
        } catch (Exception e) {
            Notification.show("Error loading chapter: " + e.getMessage(), 5000, Notification.Position.BOTTOM_CENTER);
        } finally {
            hideProgressBar();
        }
    }

    /**
     * Overridden setParameter function to fulfill the need of getting the chapterId provided in the URL field parameter.
     *
     * @param event     After view opened
     * @param parameter URL parameter
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        String chapterId = event.getRouteParameters().get("chapterId").orElse(null);

        if (chapterId != null) {
            loadChapterData(chapterId);
        } else {
            Notification.show("Nelze načíst kapitolu bez ID", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }

    /**
     * Overriden beforeLeave function to proper disposal of the model renderer to free the used RAM memory immediately.
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
}