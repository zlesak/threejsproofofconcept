package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
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

@PageTitle("Kapitola")
@Route("chapter/:chapterId?")
@Tag("chapter-view")
public class ChapterView extends Composite<VerticalLayout> implements HasUrlParameter<String>, BeforeLeaveObserver {
    private final ChapterApiClient chapterApiClient;

    TextField searchInChapterTextField = new TextField("Vyhledat v textu kapitoly");
    ComboBox chapterSelectionComboBox = new ComboBox("Kapitola");
    ProgressBar progressBar = new ProgressBar();
    EditorJs editorjs = new EditorJs();
    Three renderer = new Three();
    H1 header = new H1(); //TODO rethink the logic of main header, would keep in text not external from it

    public ChapterView(ChapterApiClient chapterApiClient) {
        this.chapterApiClient = chapterApiClient;
    /// Chapter page layout elements
        HorizontalLayout chapterPageLayout = new HorizontalLayout();
        HorizontalLayout chapterNavigation = new HorizontalLayout();
        VerticalLayout chapterNavigationColumnLayout = new VerticalLayout();
        Accordion accordion = new Accordion();
        Details details = new Details();
        VerticalLayout chapterContent = new VerticalLayout();
        HorizontalLayout chapterModel = new HorizontalLayout();
        VerticalLayout chapterModelColumnLayout = new VerticalLayout();
        Details details2 = new Details();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        renderer.getStyle().set("width", "100%");

    /// Component layout sizing
        chapterPageLayout.setWidthFull();
        chapterPageLayout.addClassName(Gap.MEDIUM);
        chapterPageLayout.setWidth("100%");
        chapterPageLayout.getStyle().set("flex-grow", "1");
        chapterNavigation.setHeightFull();
        chapterPageLayout.setFlexGrow(1.0, chapterNavigation);
        chapterNavigation.addClassName(Gap.MEDIUM);
        chapterNavigation.setWidth("min-content");
        chapterNavigation.getStyle().set("flex-grow", "1");
        chapterNavigationColumnLayout.setHeightFull();
        chapterNavigation.setFlexGrow(1.0, chapterNavigationColumnLayout);
        chapterNavigationColumnLayout.addClassName(LumoUtility.Padding.SMALL);
        chapterNavigationColumnLayout.setWidth("100%");
        chapterNavigationColumnLayout.getStyle().set("flex-grow", "1");
        accordion.setWidth("100%");
        chapterSelectionComboBox.setWidth("100%");
        details.setWidth("100%");
        chapterNavigationColumnLayout.setAlignSelf(FlexComponent.Alignment.CENTER, searchInChapterTextField);
        searchInChapterTextField.setWidth("100%");
        chapterContent.setHeightFull();
        chapterPageLayout.setFlexGrow(1.0, chapterContent);
        chapterContent.getStyle().set("flex-grow", "1");
        chapterContent.getStyle().set("flex-grow", "1");
        chapterModel.setHeightFull();
        chapterPageLayout.setFlexGrow(1.0, chapterModel);
        chapterModel.addClassName(Gap.MEDIUM);
        chapterModel.setWidth("100%");
        chapterModel.getStyle().set("flex-grow", "1");
        chapterModelColumnLayout.setHeightFull();
        chapterModel.setFlexGrow(1.0, chapterModelColumnLayout);
        chapterModelColumnLayout.getStyle().set("flex-grow", "1");
        chapterModelColumnLayout.setHeight("100%");
        details2.setWidth("100%");

    /// Filler elements setup TODO delete or move as the elements will gain its proper stated use
        setAccordionSampleData(accordion);
        setDetailsSampleData(details2);
        setDetailsSampleData(details);

    /// Chapter page layout assembly
        getContent().add(chapterPageLayout);
        chapterPageLayout.add(chapterNavigation, chapterContent, chapterModel);
        chapterNavigation.add(chapterNavigationColumnLayout);
        chapterNavigationColumnLayout.add(chapterSelectionComboBox, accordion, details, new Hr(), searchInChapterTextField);
        chapterContent.add(editorjs, new Hr());
        chapterModel.add(chapterModelColumnLayout);
        chapterModelColumnLayout.add(progressBar, renderer, new Hr(), details2);
    }

    private void setChapterSelectionComboBox(ComboBox<SubChapterForComboBox> comboBox, JsonValue subChapters) {
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

    private void setAccordionSampleData(Accordion accordion) {
        Span name = new Span("Sophia Williams");
        Span email = new Span("sophia.williams@company.com");
        Span phone = new Span("(501) 555-9128");
        VerticalLayout personalInformationLayout = new VerticalLayout(name, email, phone);
        personalInformationLayout.setSpacing(false);
        personalInformationLayout.setPadding(false);
        accordion.add("Personal information", personalInformationLayout);
        Span street = new Span("4027 Amber Lake Canyon");
        Span zipCode = new Span("72333-5884 Cozy Nook");
        Span city = new Span("Arkansas");
        VerticalLayout billingAddressLayout = new VerticalLayout();
        billingAddressLayout.setSpacing(false);
        billingAddressLayout.setPadding(false);
        billingAddressLayout.add(street, zipCode, city);
        accordion.add("Billing address", billingAddressLayout);
        Span cardBrand = new Span("Mastercard");
        Span cardNumber = new Span("1234 5678 9012 3456");
        Span expiryDate = new Span("Expires 06/21");
        VerticalLayout paymentLayout = new VerticalLayout();
        paymentLayout.setSpacing(false);
        paymentLayout.setPadding(false);
        paymentLayout.add(cardBrand, cardNumber, expiryDate);
        accordion.add("Payment", paymentLayout);
    }

    private void setDetailsSampleData(Details details) {
        Span name = new Span("Sophia Williams");
        Span email = new Span("sophia.williams@company.com");
        Span phone = new Span("(501) 555-9128");
        VerticalLayout content = new VerticalLayout(name, email, phone);
        content.setSpacing(false);
        content.setPadding(false);
        details.setSummaryText("Contact information");
        details.setOpened(true);
        details.setContent(content);
    }

    private void setMenuBarSampleData(MenuBar menuBar) {
        menuBar.addItem("View");
        menuBar.addItem("Edit");
        menuBar.addItem("Share");
        menuBar.addItem("Move");
    }
    /// UI components data update function for initializing start up values
    private void updateUIWithChapterData(ChapterEntity chapter, Resource resource) {
        header.setText(chapter.getHeader()); //TODO RETHINK
        String content = chapter.getContent();
        Notification.show("Content: " + content); //TODO REMOVE AFTER DEBUG
        editorjs.setData(content)
                .thenRun(() -> {
                    Notification.show("Content loaded successfully");
                    editorjs.getSubChaptersNames().whenComplete((subChapters, error) -> {
                        if (error != null) {
                            Notification.show("Chyba při získávání subchapters: " + error.getMessage());
                        }
                        else{
                            setChapterSelectionComboBox(chapterSelectionComboBox, subChapters);
                            editorjs.toggleReadOnlyMode(true);
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

    private void loadChapterData(String chapterId) {
        progressBar.setVisible(true);
        progressBar.setValue(0);
        try {
            ChapterEntity chapter = chapterApiClient.getChapter(chapterId);
            if (chapter != null) {
                try {
                    Resource resource = chapterApiClient.downloadModel(chapterId);
                    updateUIWithChapterData(chapter, resource);
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

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }

    @ClientCallable
    public void hideProgressBar() {
        progressBar.setVisible(false);
    }

    @ClientCallable
    public void updateProgress(double progress) {
        progressBar.setValue(progress);
        if (progress >= 1.0) {
            hideProgressBar();
        }
    }
}

