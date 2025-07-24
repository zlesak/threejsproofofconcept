package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.EditorJs;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.clients.IChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.InputStream;
import java.util.List;

@PageTitle("Vytvořit kapitolu")
@Route("createChapter")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("create-chapter")
public class CreateChapterView extends Composite<VerticalLayout> {
/// model input stream variable for potential object model
    private InputStream inputStream = null;
    private String fileName = null;

    public CreateChapterView(IChapterApiClient chapterApiClient) {
/// chapter name text field
        TextField header = new TextField("Název kapitoly");
        header.setMaxLength(255);
        header.setRequired(true);
        header.setRequiredIndicatorVisible(true);
        header.setWidthFull();
/// content field
        EditorJs editorJs = new EditorJs();
/// model upload
        UploadComponent upload = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"));
/// create button
        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();
///  createChapterButton button listener to submit the chapter with validations
        createChapterButton.addClickListener(e -> {
            String name = header.getValue().trim();

            editorJs.getData().whenComplete((body, error) -> {
                if (error != null) {
                    Notification.show("Chyba při získávání obsahu: " + error.getMessage());
                    return;
                }

                if (name.isEmpty() || body.isEmpty()) {
                    Notification.show("Vyplňte název i obsah kapitoly.");
                    return;
                }

                ChapterEntity chapter = ChapterEntity.builder()
                        .ChapterEntityName(name)
                        .ChapterEntityContent(body)
                        .build();
                ChapterEntity created;
                try {
                    if (inputStream == null || fileName == null || !fileName.toLowerCase().endsWith(".glb")) {
                        Notification.show("Musíte nahrát minimálně JEDEN 3D model s příponou .glb.");
                        return;
                    }
                    created = chapterApiClient.createChapter(chapter);
                    header.clear();
                    editorJs.clear();
                    if (created != null) {
                        try {
                            MultipartFile multipartFile = new InputStreamMultipartFile(inputStream, upload.getFileName());
                            chapterApiClient.uploadModel(multipartFile, created.getChapterEntityId());
                            upload.clearFileList();
                            inputStream = null;
                        } catch (Exception ex) {
                            Notification.show("Chyba při nahrávání modelu: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                        }
                        UI.getCurrent().navigate("chapter/" + created.getChapterEntityId());
                    }
                } catch (Exception ex) {
                    Notification.show("Chyba při vytváření kapitoly: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                    //TODO add logger to get the API client
                }
            });
        });
/// Layout setup
        VerticalLayout layoutColumn1 = new VerticalLayout();
        layoutColumn1.setWidth("80vw");
        layoutColumn1.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        layoutColumn1.add(header, editorJs, upload, createChapterButton);

        VerticalLayout centerWrapper = new VerticalLayout();
        centerWrapper.setWidthFull();
        centerWrapper.setHeightFull();
        centerWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        centerWrapper.add(layoutColumn1);
    /// scroller for dynamic addition of chapter components added by user, that resolve in dynamic height of the page
        Scroller scroller = new Scroller(centerWrapper);
        scroller.setSizeFull();

        getContent().setSizeFull();
        getContent().removeAll();
        getContent().add(scroller);
    }
}
