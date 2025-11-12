package cz.uhk.zlesak.threejslearningapp.views.model;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.components.dialogs.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.components.notifications.InfoNotification;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.services.ModelService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ModelLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("createModel")
@Tag("create-model")
@Scope("prototype")
@RolesAllowed(value = "ADMIN")
public class CreateModelView extends ModelLayout {
    private boolean skipBeforeLeaveDialog = false;
    private final ModelService modelService;

    @Autowired
    public CreateModelView(ModelService modelService) {
        super();
        this.modelService = modelService;

        Button createButton = createModelButton();
        modelUploadForm.getVl().add(createButton);
    }

    /**
     * Creates a button that uploads a model when clicked.
     * Handles both advanced (with textures) and basic model uploads.
     * Shows success/error notifications and navigates to model detail view on success.
     *
     * @return the create button
     */
    private Button createModelButton() {
        Button createButton = new Button(text("button.createModel"));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(event -> handleModelUpload());
        return createButton;
    }

    /**
     * Handles the model upload process.
     * Determines if it's an advanced or basic upload and calls the appropriate service method.
     * Shows notifications based on the result and navigates to the model detail view on success.
     */
    private void handleModelUpload() {
        try {
            QuickModelEntity quickModelEntity = uploadModel();
            showSuccessNotification();
            navigateToModelDetailView(quickModelEntity);
        } catch (ApplicationContextException e) {
            log.error("Application context error while uploading model: {}", e.getMessage(), e);
            showErrorNotification(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while uploading model", e);
            showErrorNotification(e.getMessage());
        }
    }

    /**
     * Uploads the model based on the form data.
     * Determines if it's an advanced upload (with textures) or basic upload.
     *
     * @return the uploaded model entity
     */
    private QuickModelEntity uploadModel() {
        if (modelUploadForm.getIsAdvanced().getValue()) {
            return modelService.uploadModel(
                    modelUploadForm.getModelName().getValue().trim(),
                    modelUploadForm.getObjFileUpload().getUploadedFiles().getFirst(),
                    modelUploadForm.getMainTextureFileUpload().getUploadedFiles().getFirst(),
                    modelUploadForm.getOtherTexturesFileUpload().getUploadedFiles(),
                    modelUploadForm.getCsvFileUpload().getUploadedFiles()
            );
        } else {
            return modelService.uploadModel(
                    modelUploadForm.getModelName().getValue().trim(),
                    modelUploadForm.getObjFileUpload().getUploadedFiles().getFirst()
            );
        }
    }

    /**
     * Navigates to the model detail view for the given model.
     * Uses VaadinSession to temporarily store the model data.
     * TODO: Replace session storage with proper route parameters once backend supports it
     *
     * @param quickModelEntity the model to display
     */
    private void navigateToModelDetailView(QuickModelEntity quickModelEntity) {
        skipBeforeLeaveDialog = true;
        VaadinSession.getCurrent().setAttribute("quickModelEntity", quickModelEntity);
        UI.getCurrent().navigate("model/" + quickModelEntity.getModel().getId());
    }

    private void showSuccessNotification() {
        new InfoNotification(text("notification.uploadSuccess"));
    }

    private void showErrorNotification(String errorMessage) {
        new ErrorNotification(text("notification.uploadError") + ": " + errorMessage);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (!skipBeforeLeaveDialog) {
            BeforeLeaveActionDialog.leave(event);
        }
    }

    @Override
    public String getPageTitle() {
        try {
            return text("page.title.createModelView");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
