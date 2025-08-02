package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.components.UploadLabelDiv;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import cz.uhk.zlesak.threejslearningapp.views.interfaces.IView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

@Slf4j
@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final Three renderer = new Three();
    protected final ProgressBar progressBar = new ProgressBar();
    protected final Div modelDiv = new Div(progressBar, renderer);
    @Getter
    protected final Checkbox isAdvanced = new Checkbox("Pokročilé nahrání modelu", false);
    @Getter
    protected final UploadComponent objUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"), true);
    @Getter
    protected final UploadComponent mainTextureUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), true);
    @Getter
    protected final UploadComponent otherTexturesUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), false);
    @Getter
    protected final UploadComponent csvUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".csv"), false);
    @Getter
    protected final TextField modelName;
    @Getter
    protected final VerticalLayout modelProperties;

    private final Div uploadOtherTexturesDiv;
    private Div uploadModelDiv, uploadMainTextureDiv, csvOtherTexturesDiv;
    protected String base64Model = null;
    protected String base64Texture = null;
    protected List<String> otherBase64Texture = new ArrayList<>();
    protected List<String> csvBase64 = new ArrayList<>();

    public ModelScaffold(ViewTypeEnum viewTypeEnum) {
        HorizontalLayout modelPageLayout = new HorizontalLayout();
        modelProperties = new VerticalLayout();
        VerticalLayout model = new VerticalLayout();

        modelPageLayout.setClassName("modelPageLayout");
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelProperties);
        modelPageLayout.setFlexGrow(1, model);
        modelPageLayout.add(modelProperties, model);
        modelPageLayout.setSizeFull();

        modelDiv.setId("modelDiv");
        modelDiv.setSizeFull();

        renderer.getStyle().set("width", "100%");

        modelName = new TextField("Název modelu");
        uploadModelDiv = new UploadLabelDiv(objUploadComponent, "Nahrát model");
        uploadMainTextureDiv = new UploadLabelDiv(mainTextureUploadComponent, "Hlavní textura");
        uploadOtherTexturesDiv = new UploadLabelDiv(otherTexturesUploadComponent, "Další textury");
        csvOtherTexturesDiv = new UploadLabelDiv(csvUploadComponent, "CSV soubor s popisem textur");

        modelProperties.add(modelName, isAdvanced, uploadModelDiv, uploadMainTextureDiv, uploadOtherTexturesDiv, csvOtherTexturesDiv);

        model.add(modelDiv);

        switch (viewTypeEnum) {
            case CREATE -> {
                modelName.setLabel("Název modelu");
                modelName.setPlaceholder("Zadejte název modelu");
                hideAdvancedModelUpload();
                showProgressBar(false);
            }
            case EDIT -> {

            }
            case VIEW -> modelName.setVisible(false);
        }

        isAdvanced.setTooltipText("Pokročilé nahrání modelu umožňuje nahrát model ve formátu OBJ s jednou hlavní a dalšími texturami pro pokročilé modely.");
        isAdvanced.addValueChangeListener(e -> {
            if (e.getValue()) {
                showAdvancedModelUpload();
            } else {
                hideAdvancedModelUpload();
            }
        });

        objUploadComponent.addSucceededListener(event -> {
            isAdvanced.setReadOnly(true);
            if (!isAdvanced.getValue()) {
                String fileName = event.getFileName();
                InputStream inputStream = ((MultiFileMemoryBuffer) objUploadComponent.getReceiver()).getInputStream(fileName);
                if (inputStream != null) {
                    try {
                        byte[] bytes = inputStream.readAllBytes();
                        base64Model = Base64.getEncoder().encodeToString(bytes);
                        renderer.loadModel(base64Model);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                String fileName = event.getFileName();
                InputStream inputStream = ((MultiFileMemoryBuffer) objUploadComponent.getReceiver()).getInputStream(fileName);
                if (inputStream != null) {
                    try {
                        byte[] bytes = inputStream.readAllBytes();
                        base64Model = Base64.getEncoder().encodeToString(bytes);
                        if (base64Texture != null) {
                            renderer.loadAdvancedModel(base64Model, base64Texture);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        objUploadComponent.addFileRemovedListener(event -> {
            renderer.clear();
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
                        renderer.loadAdvancedModel(base64Model, base64Texture);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        mainTextureUploadComponent.addFileRemovedListener(event -> {
            renderer.clear();
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
                    ex.printStackTrace();
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
                    ex.printStackTrace();
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
                    ex.printStackTrace();
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
                    ex.printStackTrace();
                }
            }
        });

        getContent().add(modelPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");
    }

    private void showAdvancedModelUpload() {
        objUploadComponent.setAcceptedFileTypes(List.of(".obj"));
        uploadMainTextureDiv.setVisible(true);
        uploadOtherTexturesDiv.setVisible(true);
        csvOtherTexturesDiv.setVisible(true);
        uploadOtherTexturesDiv.setEnabled(false);
        csvOtherTexturesDiv.setEnabled(false);
    }

    private void hideAdvancedModelUpload() {
        objUploadComponent.setAcceptedFileTypes(List.of(".glb"));
        uploadMainTextureDiv.setVisible(false);
        uploadOtherTexturesDiv.setVisible(false);
        csvOtherTexturesDiv.setVisible(false);
    }


    public void showRendererAndProgressBar(Boolean show) {
        renderer.setVisible(show);
        progressBar.setVisible(show);
    }

    protected void showProgressBar(Boolean show) {
        progressBar.setVisible(show);
        if (show) {
            progressBar.setIndeterminate(true);
            progressBar.setWidthFull();
        }
    }

    protected void showRenderer(Boolean show) {
        renderer.setVisible(show);
    }
}