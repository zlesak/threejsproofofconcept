package cz.uhk.zlesak.threejslearningapp.components.compositions;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.components.UploadLabelDiv;
import cz.uhk.zlesak.threejslearningapp.events.ModelClearEvent;
import cz.uhk.zlesak.threejslearningapp.events.ModelLoadEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * ModelUploadFormScroller is a custom Vaadin component that provides a form for uploading 3D models and their associated textures.
 * It extends the Scroller component to allow vertical scrolling of the form content.
 * The form includes fields for the model name, a checkbox for advanced upload options, and upload components for the model file and textures.
 * It also includes event handling for successful uploads and file removals, allowing integration with other parts of the application.
 */
@Slf4j
public class ModelUploadFormScrollerComposition extends Scroller {
    @Getter
    protected final VerticalLayout vl = new VerticalLayout();
    @Getter
    protected final TextField modelName;
    @Getter
    protected final Checkbox isAdvanced;
    protected final UploadLabelDiv uploadModelDiv, uploadMainTextureDiv, uploadOtherTexturesDiv, csvOtherTexturesDiv;
    @Getter
    protected final UploadComponent objUploadComponent, mainTextureUploadComponent, otherTexturesUploadComponent, csvUploadComponent;

    protected String base64Model = null;
    protected String base64Texture = null;
    protected List<String> otherBase64Texture = new ArrayList<>();
    protected List<String> csvBase64 = new ArrayList<>();

    /**
     * Constructor for ModelUploadFormScroller.
     * Initializes the form components, sets up event listeners for upload success and file removal,
     * and configures the layout and visibility of the form elements.
     */
    public ModelUploadFormScrollerComposition() {
        super(Scroller.ScrollDirection.VERTICAL);
        setContent(vl);

        objUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"), true, true);
        mainTextureUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), true, true);
        otherTexturesUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), false, true);
        csvUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".csv"), false, false);

        modelName = new TextField("Název modelu");
        modelName.setPlaceholder("Zadejte název modelu");

        isAdvanced = new Checkbox("Pokročilé nahrání modelu", false);
        isAdvanced.setTooltipText("Pokročilé nahrání modelu umožňuje nahrát model ve formátu OBJ s jednou hlavní a dalšími texturami pro pokročilé modely.");
        isAdvanced.addValueChangeListener(e -> {
            if (e.getValue()) {
                showAdvancedModelUpload();
            } else {
                hideAdvancedModelUpload();
            }
        });

        uploadModelDiv = new UploadLabelDiv(objUploadComponent, "Nahrát model");
        uploadMainTextureDiv = new UploadLabelDiv(mainTextureUploadComponent, "Hlavní textura");
        uploadOtherTexturesDiv = new UploadLabelDiv(otherTexturesUploadComponent, "Další textury");
        csvOtherTexturesDiv = new UploadLabelDiv(csvUploadComponent, "CSV soubor s popisem textur");

        objUploadComponent.addSucceededListener(event -> {
            isAdvanced.setReadOnly(true);
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) objUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    base64Model = Base64.getEncoder().encodeToString(bytes);
                    if (!isAdvanced.getValue()) {
                        fireEvent(new ModelLoadEvent(this, base64Model, null));
                    } else {
                        if (base64Texture != null) {
                            fireEvent(new ModelLoadEvent(this, base64Model, base64Texture));
                        }
                    }
                } catch (Exception ex) {
                    log.error("Chyba při zpracování modelu", ex);
                }
            }
        });
        objUploadComponent.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            isAdvanced.setReadOnly(false);
            mainTextureUploadComponent.clear();
            otherTexturesUploadComponent.clear();
            base64Model = null;
            base64Texture = null;
            otherBase64Texture.clear();
        });

        mainTextureUploadComponent.addSucceededListener(event -> {
            isAdvanced.setReadOnly(true);
            uploadOtherTexturesDiv.setEnabled(true);
            csvOtherTexturesDiv.setEnabled(true);
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) mainTextureUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    base64Texture = Base64.getEncoder().encodeToString(bytes);
                    if (base64Model != null) {
                        fireEvent(new ModelLoadEvent(this, base64Model, base64Texture));
                    }
                } catch (Exception ex) {
                    log.error("Chyba při zpracování hlavní textury", ex);
                }
            }
        });
        mainTextureUploadComponent.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            uploadOtherTexturesDiv.setEnabled(false);
            csvOtherTexturesDiv.setEnabled(false);
            base64Texture = null;
        });

        otherTexturesUploadComponent.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) otherTexturesUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    otherBase64Texture.add(base64);
                } catch (Exception ex) {
                    log.error("Chyba při zpracování další textury", ex);
                }
            }
        });
        otherTexturesUploadComponent.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) otherTexturesUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    csvBase64.remove(base64);
                } catch (Exception ex) {
                    log.error("Chyba při zpracování další textury", ex);
                }
            }
        });

        csvUploadComponent.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) otherTexturesUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    csvBase64.add(base64);
                } catch (Exception ex) {
                    log.error("Chyba při zpracování CSV souboru", ex);
                }
            }
        });
        csvUploadComponent.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = ((MultiFileMemoryBuffer) otherTexturesUploadComponent.getReceiver()).getInputStream(fileName);
            if (inputStream != null) {
                try {
                    byte[] bytes = inputStream.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    otherBase64Texture.remove(base64);
                } catch (Exception ex) {
                    log.error("Chyba při zpracování CSV souboru", ex);
                }
            }
        });

        hideAdvancedModelUpload();

        modelName.setWidthFull();
        modelName.getStyle().set("min-width", "0");
        isAdvanced.getStyle().set("white-space", "nowrap");
        HorizontalLayout topHorizontalLayout = new HorizontalLayout(modelName, isAdvanced);
        topHorizontalLayout.setWidthFull();
        topHorizontalLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, isAdvanced);
        topHorizontalLayout.setFlexGrow(1, modelName);
        topHorizontalLayout.setFlexGrow(0, isAdvanced);
        topHorizontalLayout.getStyle().set("min-width", "0");

        vl.setWidthFull();
        vl.add(topHorizontalLayout, uploadModelDiv, uploadMainTextureDiv, uploadOtherTexturesDiv, csvOtherTexturesDiv);
    }

    /**
     * Shows the advanced model upload options, allowing the user to upload an OBJ model with multiple textures.
     * Adjusts the accepted file types and visibility of texture upload components accordingly.
     */
    private void showAdvancedModelUpload() {
        objUploadComponent.setAcceptedFileTypes(List.of(".obj"));
        uploadMainTextureDiv.setVisible(true);
        uploadOtherTexturesDiv.setVisible(true);
        csvOtherTexturesDiv.setVisible(true);
        uploadOtherTexturesDiv.setEnabled(false);
        csvOtherTexturesDiv.setEnabled(false);
    }

    /**
     * Hides the advanced model upload options, restricting the user to upload only a GLB model.
     * Adjusts the accepted file types and visibility of texture upload components accordingly.
     */
    private void hideAdvancedModelUpload() {
        objUploadComponent.setAcceptedFileTypes(List.of(".glb"));
        uploadMainTextureDiv.setVisible(false);
        uploadOtherTexturesDiv.setVisible(false);
        csvOtherTexturesDiv.setVisible(false);
    }

    /**
     * Sets the form to listing mode, making all input fields read-only and disabling upload components.
     * This mode is intended for viewing existing models without the ability to modify them.
     */
    public void listingMode() {
        modelName.setReadOnly(true);
        isAdvanced.setReadOnly(true);
        objUploadComponent.setEnabled(false);
        uploadModelDiv.setVisible(false);
        mainTextureUploadComponent.setEnabled(false);
        uploadMainTextureDiv.setVisible(false);
        otherTexturesUploadComponent.setEnabled(false);
        uploadOtherTexturesDiv.setVisible(false);
        csvUploadComponent.setEnabled(false);
        csvOtherTexturesDiv.setVisible(false);
    }

    /**
     * Adds a listener for model load events.
     * This listener will be notified when a model is successfully loaded.
     *
     * @param listener the listener to be added
     */
    public void addModelLoadEventListener(ComponentEventListener<ModelLoadEvent> listener) {
        addListener(ModelLoadEvent.class, listener);
    }

    /**
     * Adds a listener for model clear events.
     * This listener will be notified when the model is cleared from the form.
     *
     * @param listener the listener to be added
     */
    public void addModelClearEventListener(ComponentEventListener<ModelClearEvent> listener) {
        addListener(ModelClearEvent.class, listener);
    }
}
