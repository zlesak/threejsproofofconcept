package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.EditorJs;
import cz.uhk.zlesak.threejslearningapp.controlers.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.InputStream;

@PageTitle("Vytvořit kapitolu")
@Route("createChapter")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("create-chapter")
public class CreateChapterView extends Composite<VerticalLayout> {
    /// model input stream variable for potential object model
    private InputStream inputStream = null;
    private String fileName = null;

    public CreateChapterView(ChapterApiClient chapterApiClient) {
/// header text field
        TextField header = new TextField("Název kapitoly");
        header.setMaxLength(255);
        header.setRequired(true);
        header.setRequiredIndicatorVisible(true);
        header.setWidthFull();
/// content field
        EditorJs editorJs = new EditorJs();
/// model upload
        Upload upload = getUpload();
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
                        .header(name)
                        .content(body)
                        .modelPath("")
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
                            MultipartFile multipartFile = new InputStreamMultipartFile(inputStream, "model.gltf", inputStream.available());
                            chapterApiClient.uploadModel(multipartFile, created.getId());
                            upload.clearFileList();
                            inputStream = null;
                        } catch (Exception ex) {
                            Notification.show("Chyba při nahrávání modelu: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                        }
                        UI.getCurrent().navigate("chapter/" + created.getId());
                    }
                } catch (Exception ex) {
                    Notification.show("Chyba při vytváření kapitoly: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                }
            });
        });
/// Layout setup
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn1 = new VerticalLayout();
        layoutColumn1.setWidth("80vw");

        layoutRow.add(layoutColumn1);
        layoutColumn1.add(header, editorJs, upload, createChapterButton);

        getContent().getStyle().set("flex-grow", "1");
        getContent().setSizeFull();
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        getContent().setFlexGrow(1.0, layoutRow);

        getContent().add(layoutRow);
    }

    /// Method to get the upload component with file validation and maximum file size of 50 MB
    private Upload getUpload() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFileSize(50 * 1024 * 1024);
        upload.setAcceptedFileTypes(".glb");

        upload.addSucceededListener(event -> {
            this.fileName = event.getFileName();
            this.inputStream = buffer.getInputStream(fileName);
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        return upload;
    }
}
