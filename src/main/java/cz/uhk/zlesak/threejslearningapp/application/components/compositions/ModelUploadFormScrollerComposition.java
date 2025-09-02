package cz.uhk.zlesak.threejslearningapp.application.components.compositions;

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
import cz.uhk.zlesak.threejslearningapp.application.components.NameTextField;
import cz.uhk.zlesak.threejslearningapp.application.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.UploadLabelDiv;
import cz.uhk.zlesak.threejslearningapp.application.events.*;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
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
    private final Map<String, QuickTextureEntity> quickTextureEntityMap = new HashMap<>();
    private final Map<String, String> csvMap = new HashMap<>();
    protected String modelUrl = null;
    protected String textureUrl = null;
    protected List<String> otherTexturesUrls = new ArrayList<>();
    protected List<String> csvBase64 = new ArrayList<>();

    /**
     * Constructor for ModelUploadFormScroller.
     * Initializes the form components, sets up event listeners for upload success and file removal,
     * and configures the layout and visibility of the form elements.
     */
    public ModelUploadFormScrollerComposition() {
        super(Scroller.ScrollDirection.VERTICAL);
        setContent(vl);
        objUploadComponent = new UploadComponent(List.of(".glb"), true, true);
        mainTextureUploadComponent = new UploadComponent(List.of(".jpg"), true, true);
        otherTexturesUploadComponent = new UploadComponent(List.of(".jpg"), false, true);
        csvUploadComponent = new UploadComponent(List.of(".csv"), false, false);

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

        uploadModelDiv = new UploadLabelDiv(objUploadComponent, "Nahrát model");
        uploadMainTextureDiv = new UploadLabelDiv(mainTextureUploadComponent, "Hlavní textura");
        uploadOtherTexturesDiv = new UploadLabelDiv(otherTexturesUploadComponent, "Další textury");
        csvOtherTexturesDiv = new UploadLabelDiv(csvUploadComponent, "CSV soubor s popisem textur");

        objUploadComponent.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    isAdvanced.setReadOnly(true);
                    String contentType;
                    if (fileName.toLowerCase().endsWith(".obj") || isAdvanced.getValue()) {
                        contentType = "text/plain";
                    } else {
                        contentType = "model/gltf-binary";
                    }
                    modelUrl = registerStreamUrl(fileName, contentType, inputStreamMultipartFile.getInputStream());
                    if (!isAdvanced.getValue()) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, null));
                    } else if (textureUrl != null) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, textureUrl));
                    }
                }
        );

        objUploadComponent.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            isAdvanced.setReadOnly(false);
            mainTextureUploadComponent.clear();
            otherTexturesUploadComponent.clear();
            modelUrl = null;
            textureUrl = null;
            otherTexturesUrls.clear();
        });

        mainTextureUploadComponent.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    isAdvanced.setReadOnly(true);
                    uploadOtherTexturesDiv.setEnabled(true);
                    csvOtherTexturesDiv.setEnabled(true);
                    textureUrl = registerStreamUrl(fileName, "image/jpeg", inputStreamMultipartFile.getInputStream());
                    if (modelUrl != null) {
                        fireEvent(new ModelLoadEvent(this, modelUrl, textureUrl));
                        fireEvent(new ModelTextureChangeEvent(this, textureUploaded(fileName)));
                    }
                }
        );

        mainTextureUploadComponent.addFileRemovedListener(event -> {
            fireEvent(new ModelClearEvent(this));
            fireEvent(new ModelTextureChangeEvent(this, textureDeleted(event.getFileName())));
            uploadOtherTexturesDiv.setEnabled(false);
            csvOtherTexturesDiv.setEnabled(false);
            textureUrl = null;
        });

        otherTexturesUploadComponent.setUploadListener(
                (fileName, inputStreamMultipartFile) -> {
                    textureUrl = registerStreamUrl(fileName, "image/jpeg", inputStreamMultipartFile.getInputStream());
                    otherTexturesUrls.add(textureUrl);
                    Map<String, String> otherTextures = new HashMap<>();
                    otherTextures.put(fileName, textureUrl);
                    fireEvent(new OtherTextureLoadedEvent(this, otherTextures));
                    fireEvent(new ModelTextureChangeEvent(this, textureUploaded(fileName)));
                });

        otherTexturesUploadComponent.addFileRemovedListener(event -> {
            fireEvent(new OtherTextureRemovedEvent(this, event.getFileName()));
            fireEvent(new ModelTextureChangeEvent(this, textureDeleted(event.getFileName())));
        });


        csvUploadComponent.setUploadListener(
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

        csvUploadComponent.addFileRemovedListener(event -> {
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
     * @param fileName name of the file
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
     * @return the current instance of ModelUploadFormScrollerComposition for method chaining
     */
    public ModelUploadFormScrollerComposition addModelLoadEventListener(ComponentEventListener<ModelLoadEvent> listener) {
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
    public ModelUploadFormScrollerComposition addModelClearEventListener(ComponentEventListener<ModelClearEvent> listener) {
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
    public ModelUploadFormScrollerComposition addOtherTextureLoadedEventListener(ComponentEventListener<OtherTextureLoadedEvent> listener) {
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
    public ModelUploadFormScrollerComposition addOtherTextureRemovedEventListener(ComponentEventListener<OtherTextureRemovedEvent> listener) {
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
     * Handles the event when a texture is uploaded.
     * It updates the internal map of textures and returns the current list of QuickTextureEntity objects
     *
     * @param name the name of the uploaded texture file
     * @return the current list of QuickTextureEntity objects
     * @see ThreeJsComponent#addOtherTextures(Map)
     */
    private List<QuickTextureEntity> textureUploaded(String name) {
        if (!this.quickTextureEntityMap.containsKey(name)) {
            this.quickTextureEntityMap.put(name, new QuickTextureEntity(name, name, this.csvMap.getOrDefault(name, null)));
        }
        return this.quickTextureEntityMap.values().stream().toList();
    }

    /**
     * Handles the event when a texture is deleted.
     *
     * @param name the name of the deleted texture file
     * @return the current list of QuickTextureEntity objects after deletion
     * @see ThreeJsComponent#removeOtherTexture(String)
     */
    private List<QuickTextureEntity> textureDeleted(String name) {
        this.quickTextureEntityMap.remove(name);
        return this.quickTextureEntityMap.values().stream().toList();
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
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap.values().stream().toList()));
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
            fireEvent(new ModelTextureChangeEvent(this, this.quickTextureEntityMap.values().stream().toList()));
        }
    }
}
