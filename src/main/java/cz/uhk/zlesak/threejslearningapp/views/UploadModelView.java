package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.UploadModelController;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.InputStream;
import java.util.List;

@PageTitle("Nahrát 3D model")
@Route("uploadModel")
@Menu(order = 2, icon = LineAwesomeIconUrl.CUBE_SOLID)
@Tag("upload-model")
public class UploadModelView extends Composite<VerticalLayout> {
    private final UploadModelController uploadModelController;
    private InputStream inputStream = null;

    @Autowired
    public UploadModelView(UploadModelController uploadModelController) {
        this.uploadModelController = uploadModelController;
        ModelApiClient modelApiClient = new ModelApiClient();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidth("40vw");
        formLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);

        TextField nameField = new TextField("Název modelu");
        nameField.setMaxLength(255);
        nameField.setRequired(true);
        nameField.setRequiredIndicatorVisible(true);
        nameField.setWidthFull();

        UploadComponent upload = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"));

        Button uploadButton = new Button("Nahrát model");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.setWidthFull();

        formLayout.add(nameField, upload, uploadButton);

        VerticalLayout modelPreviewLayout = new VerticalLayout();
        modelPreviewLayout.setWidthFull();
        modelPreviewLayout.setHeightFull();
        modelPreviewLayout.setPadding(false);

        Div modelDiv = new Div(progressBar);
        modelDiv.setId("modelDiv");
        modelDiv.setWidthFull();
        modelDiv.setHeight("85vh");

        modelPreviewLayout.add(modelDiv);

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.add(formLayout, modelPreviewLayout);

        getContent().removeAll();
        getContent().add(mainLayout);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        uploadButton.addClickListener(e -> {
            try {
                String name = nameField.getValue().trim();

                InputStreamMultipartFile multipartFile = new InputStreamMultipartFile(upload.getInputStream(), upload.getFileName());

                if (name.isEmpty()) {
                    Notification.show("Vyplňte název modelu.");
                    return;
                }
                if (multipartFile.isEmpty()) {
                    Notification.show("Nahrajte soubor");
                    return;
                }
                uploadModelController.uploadModel(name, multipartFile);

                upload.clearFileList();
                inputStream = null;
                nameField.clear();
                Notification.show("Model úspěšně nahrán.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("models");
            } catch (Exception ex) {
                Notification.show("Chyba při nahrávání modelu: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
    }
}
