package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.controlers.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@PageTitle("Kapitola")
@Route("chapter/:chapterId?")
@Tag("chapter-view")
public class ChapterView extends Composite<VerticalLayout> implements HasUrlParameter<String>, BeforeLeaveObserver  {
    ProgressBar progressBar = new ProgressBar();
    Three renderer = new Three();
    private final ChapterApiClient chapterApiClient;

    H2 h2 = new H2();
    Paragraph textMedium = new Paragraph();

    public ChapterView(ChapterApiClient chapterApiClient) {
        this.chapterApiClient = chapterApiClient;
//set the id
        getContent().setId("chapter-container");
//definition
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        Icon icon = new Icon();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        Div div = new Div();

//styling
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn3.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");
        renderer.getStyle().set("width", "100%");
        renderer.getStyle().set("height", "100%");
        icon.setIcon("lumo:user");
        icon.getStyle().set("flex-grow", "1");
        layoutColumn4.addClassName(Gap.SMALL);
        layoutColumn4.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn4.setAlignItems(Alignment.START);
        h2.setWidth("100%");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
//placing elements
        getContent().add(layoutRow);
//renderer
        layoutRow.add(layoutColumn3);
        div.add(progressBar, renderer);
        div.setSizeFull();
        layoutColumn3.add(div);
        progressBar.getStyle().set("position", "absolute")
                .set("width", "40%")
                .set("top", "50%")
                .set("left", "50%")
                .set("transform", "translate(-50%, -50%)")
                .set("z-index", "10");
        div.getStyle().set("position", "relative");
//study text
        layoutRow.add(layoutColumn4);
        layoutColumn4.add(h2);
        layoutColumn4.add(textMedium);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }

    private void updateUIWithChapterData(ChapterEntity chapter, Resource resource) {
        h2.setText(chapter.getHeader());
        textMedium.setText(chapter.getContent());

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
}

