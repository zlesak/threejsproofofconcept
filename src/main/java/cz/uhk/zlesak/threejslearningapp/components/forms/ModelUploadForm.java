package cz.uhk.zlesak.threejslearningapp.components.forms;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.components.inputs.textFields.NameTextField;
import cz.uhk.zlesak.threejslearningapp.components.inputs.files.FileUpload;
import cz.uhk.zlesak.threejslearningapp.components.containers.UploadLabelContainer;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelClearEvent;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelLoadEvent;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelTextureChangeEvent;
import cz.uhk.zlesak.threejslearningapp.events.texture.OtherTextureLoadedEvent;
import cz.uhk.zlesak.threejslearningapp.events.texture.OtherTextureRemovedEvent;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ModelUploadFormScroller is a custom Vaadin component that provides a form for uploading 3D models and their associated textures.
 * It extends the Scroller component to allow vertical scrolling of the form content.
 * The form includes fields for the model name, a checkbox for advanced upload options, and upload components for the model file and textures.
 * It also includes event handling for successful uploads and file removals, allowing integration with other parts of the application.
 */
@Slf4j
public class ModelUploadForm extends Scroller {
    @Getter
    protected final VerticalLayout vl = new VerticalLayout();
    @Getter
    protected final TextField modelName;
    @Getter
    protected final Checkbox isAdvanced;
    protected final UploadLabelContainer uploadModelDiv, uploadMainTextureDiv, uploadOtherTexturesDiv, csvOtherTexturesDiv;
    @Getter
    protected final FileUpload objFileUpload, mainTextureFileUpload, otherTexturesFileUpload, csvFileUpload;
    private final Map<String, QuickTextureEntity> quickTextureEntityMap = new HashMap<>();
    private final Map<String, String> csvMap = new HashMap<>();
    protected String modelUrl = null;
    protected String textureUrl = null;
    private String textureName = null;
    private String modelFileName = null;
    protected List<String> otherTexturesUrls = new ArrayList<>();
    protected List<String> csvBase64 = new ArrayList<>();

    /**
     * Constructor for ModelUploadFormScroller.
     * Initializes the form components, sets up event listeners for upload success and file removal,
     * and configures the layout and visibility of the form elements.
     */
    public ModelUploadForm() {
        super(Scroller.ScrollDirection.VERTICAL);
        setContent(vl);
        objFileUpload = new FileUpload(List.of(".glb"), true, true);
        mainTextureFileUpload = new FileUpload(List.of(".jpg"), true, true);
        otherTexturesFileUpload = new FileUpload(List.of(".jpg"), false, true);
        csvFileUpload = new FileUpload(List.of(".csv"), false, false);

        modelName = new NameTextField("Zadejte název modelu");

        isAdvanced = new Checkbox("Pokročilé nahrání modelu", false);
        isAdvanced.setTooltipText("Pokročilé nahrání modelu umožňuje nahrát model ve formátu OBJ s jednou hlavní a dalšími texturami pro pokročilé modely.");
        isAdvanced.addValueChangeListener(e -> {
            if (e.getValue()) {
                showAdvancedModelUpload();
            } else {
                hideAdvancedModelUpload();
            }
        });

        uploadModelDiv = new UploadLabelContainer(objFileUpload, "Nahrát model");
        uploadMainTextureDiv = new UploadLabelContainer(mainTextureFileUpload, "Hlavní textura");
        uploadOtherTexturesDiv = new UploadLabelContainer(otherTexturesFileUpload, "Další textury");
        csvOtherTexturesDiv = new UploadLabelContainer(csvFileUpload, "CSV soubor s popisem textur");

        objFileUpload.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    isAdvanced.setReadOnly(true);
                    String contentType;
                    if (fileName.toLowerCase().endsWith(".obj") || isAdvanced.getValue()) {
                        contentType = "text/plain";
                    } else {
                        contentType = "model/gltf-binary";
                    }
                    modelUrl = registerStreamUrl(fileName, contentType, inputStreamMultipartFile.getInputStream());

                    modelFileName = fileName;

                    if (!isAdvanced.getValue()) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, null, "modelId", modelFileName, null));
                    } else if (textureUrl != null) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, textureUrl, "modelId", modelFileName, textureName));
                    }
                }
        );

        objFileUpload.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            isAdvanced.setReadOnly(false);
            mainTextureFileUpload.clear();
            otherTexturesFileUpload.clear();
            modelUrl = null;
            textureUrl = null;
            otherTexturesUrls.clear();
        });

        mainTextureFileUpload.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    isAdvanced.setReadOnly(true);
                    uploadOtherTexturesDiv.setEnabled(true);
                    csvOtherTexturesDiv.setEnabled(true);
                    textureUrl = registerStreamUrl(fileName, "image/jpeg", inputStreamMultipartFile.getInputStream());
                    textureName = fileName;
                    if (modelUrl != null) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, textureUrl, "modelId", modelFileName, textureName));
                        this.quickTextureEntityMap.put("main", new QuickTextureEntity(fileName, fileName, this.csvMap.getOrDefault(fileName, null)));
                        fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
                    }
                }
        );

        mainTextureFileUpload.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            this.quickTextureEntityMap.remove("main");
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
            uploadOtherTexturesDiv.setEnabled(false);
            csvOtherTexturesDiv.setEnabled(false);
            textureUrl = null;
        });

        otherTexturesFileUpload.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    textureUrl = registerStreamUrl(fileName, "image/jpeg", inputStreamMultipartFile.getInputStream());
                    otherTexturesUrls.add(textureUrl);
                    Map<String, String> otherTextures = new HashMap<>();
                    otherTextures.put(fileName, textureUrl);
                    fireEvent(new OtherTextureLoadedEvent(this, otherTextures));
                    this.quickTextureEntityMap.put(fileName, new QuickTextureEntity(fileName, fileName, this.csvMap.getOrDefault(fileName, null)));
                    fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
                });

        otherTexturesFileUpload.addFileRemovedListener(event -> {
            fireEvent(new OtherTextureRemovedEvent(this, event.getFileName()));
            this.quickTextureEntityMap.remove(event.getFileName());
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
        });


        csvFileUpload.setUploadListener(
                (fileName, uploadedMultipartFile) -> {
                    String base64;
                    try (InputStream inputStream = uploadedMultipartFile.getInputStream()) {
                        base64 = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
                        csvBase64.add(base64);
                        String csvContent = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
                        csvUploaded(fileName, csvContent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        csvFileUpload.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            csvDeleted(fileName);
        });

        hideAdvancedModelUpload();

        modelName.setWidthFull();
        modelName.getStyle().set("min-width", "0");
        isAdvanced.getStyle().set("white-space", "nowrap");
        HorizontalLayout topHorizontalLayout = new HorizontalLayout(modelName, isAdvanced);
        topHorizontalLayout.setWidthFull();
        topHorizontalLayout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, isAdvanced);
        topHorizontalLayout.setFlexGrow(1, modelName);
        topHorizontalLayout.setFlexGrow(0, isAdvanced);
        topHorizontalLayout.getStyle().set("min-width", "0");

        vl.setWidthFull();
        vl.setPadding(false);
        vl.add(topHorizontalLayout, uploadModelDiv, uploadMainTextureDiv, uploadOtherTexturesDiv, csvOtherTexturesDiv);
    }

    /**
     * Registers resource URL to provide files via streaming endpoint from front end side.
     *
     * @param fileName    name of the file
     * @param contentType content type of the file
     * @param inputStream file in input stream format
     * @return registered stream URL in Vaadin session
     */
    private String registerStreamUrl(String fileName, String contentType, InputStream inputStream) {
        StreamResource resource = new StreamResource(fileName, () -> inputStream);
        resource.setContentType(contentType);
        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        return registration.getResourceUri().toString();
    }

    /**
     * Converts a given filename to a .jpg filename by replacing its extension.
     * If the filename has no extension, it appends .jpg to the end.
     *
     * @param filename the original filename
     * @return the filename with a .jpg extension
     */
    private static String toJpgName(String filename) {
        int dot = filename.lastIndexOf('.');
        String base = dot > 0 ? filename.substring(0, dot) : filename;
        return base + ".jpg";
    }

    /**
     * Shows the advanced model upload options, allowing the user to upload an OBJ model with multiple textures.
     * Adjusts the accepted file types and visibility of texture upload components accordingly.
     */
    private void showAdvancedModelUpload() {
        objFileUpload.setAcceptedFileTypes(List.of(".obj"));
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
        objFileUpload.setAcceptedFileTypes(List.of(".glb"));
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
        objFileUpload.setEnabled(false);
        uploadModelDiv.setVisible(false);
        mainTextureFileUpload.setEnabled(false);
        uploadMainTextureDiv.setVisible(false);
        otherTexturesFileUpload.setEnabled(false);
        uploadOtherTexturesDiv.setVisible(false);
        csvFileUpload.setEnabled(false);
        csvOtherTexturesDiv.setVisible(false);
    }

    /**
     * Adds a listener for model load events.
     * This listener will be notified when a model is successfully loaded.
     *
     * @param listener the listener to be added
     * @return the current instance of ModelUploadFormScrollerComposition for method chaining
     */
    public ModelUploadForm addModelLoadEventListener(ComponentEventListener<ModelLoadEvent> listener) {
        addListener(ModelLoadEvent.class, listener);
        return this;
    }

    /**
     * Adds a listener for model clear events.
     * This listener will be notified when the model is cleared from the form.
     *
     * @param listener the listener to be added
     * @return the current instance of ModelUploadFormScrollerComposition for method chaining
     */
    public ModelUploadForm addModelClearEventListener(ComponentEventListener<ModelClearEvent> listener) {
        addListener(ModelClearEvent.class, listener);
        return this;
    }

    /**
     * Adds a listener for other texture loaded events.
     * This listener will be notified when additional textures are successfully loaded.
     *
     * @param listener the listener to be added
     * @return the current instance of ModelUploadFormScrollerComposition for method chaining
     */
    public ModelUploadForm addOtherTextureLoadedEventListener(ComponentEventListener<OtherTextureLoadedEvent> listener) {
        addListener(OtherTextureLoadedEvent.class, listener);
        return this;
    }

    /**
     * Adds a listener for other texture removed events.
     * This listener will be notified when additional textures are removed.
     *
     * @param listener the listener to be added
     * @return the current instance of ModelUploadFormScrollerComposition for method chaining
     */
    public ModelUploadForm addOtherTextureRemovedEventListener(ComponentEventListener<OtherTextureRemovedEvent> listener) {
        addListener(OtherTextureRemovedEvent.class, listener);
        return this;
    }

    /**
     * Adds a listener for model texture change events.
     * This listener will be notified when textures are uploaded or removed.
     *
     * @param listener the listener to be added
     */
    public void addTextureChangeEventListener(ComponentEventListener<ModelTextureChangeEvent> listener) {
        addListener(ModelTextureChangeEvent.class, listener);
    }

    /**
     * Handles the event when a CSV file is uploaded.
     *
     * @param name       the name of the uploaded CSV file
     * @param csvContent the content of the uploaded CSV file
     */
    private void csvUploaded(String name, String csvContent) {
        String key = toJpgName(name);
        this.csvMap.put(key, csvContent);
        if (this.quickTextureEntityMap.containsKey(key)) {
            this.quickTextureEntityMap.get(key).setCsvContent(csvContent);
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
        }
    }

    /**
     * Handles the event when a CSV file is deleted.
     *
     * @param name the name of the deleted CSV file
     */
    private void csvDeleted(String name) {
        String key = toJpgName(name);
        this.csvMap.remove(key);
        if (this.quickTextureEntityMap.containsKey(key)) {
            this.quickTextureEntityMap.get(key).setCsvContent(null);
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap));
        }
    }
}
