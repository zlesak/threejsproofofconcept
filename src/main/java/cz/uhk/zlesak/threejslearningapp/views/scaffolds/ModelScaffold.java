package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.components.UploadLabelDiv;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final ThreeJsComponent renderer = new ThreeJsComponent();
    protected final ProgressBar progressBar = new ProgressBar();
    protected final Div modelDiv = new Div(progressBar, renderer);
    @Getter
    protected final Checkbox isAdvanced = new Checkbox("Pokročilé nahrání modelu", false);
    @Getter
    protected final UploadComponent objUploadComponent;
    @Getter
    protected final UploadComponent mainTextureUploadComponent;
    @Getter
    protected final UploadComponent otherTexturesUploadComponent;
    @Getter
    protected final UploadComponent csvUploadComponent;
    @Getter
    protected final TextField modelName;
    @Getter
    protected final VerticalLayout modelProperties;
    protected final Scroller modelPropertiesScroller;

    private final Div uploadOtherTexturesDiv;
    private final Div uploadModelDiv, uploadMainTextureDiv, csvOtherTexturesDiv;
    protected String base64Model = null;
    protected String base64Texture = null;
    protected List<String> otherBase64Texture = new ArrayList<>();
    protected List<String> csvBase64 = new ArrayList<>();
    protected final I18NProvider i18nProvider;

    public ModelScaffold(I18NProvider i18nProvider, ViewTypeEnum viewTypeEnum) {
        this.i18nProvider = i18nProvider;
        objUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"), true);
        mainTextureUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), true);
        otherTexturesUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), false);
        csvUploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".csv"), false);
        HorizontalLayout modelPageLayout = new HorizontalLayout();
        modelProperties = new VerticalLayout();
        VerticalLayout model = new VerticalLayout();
        modelPropertiesScroller = new Scroller(modelProperties, Scroller.ScrollDirection.VERTICAL);

        modelPageLayout.setClassName("modelPageLayout");
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelProperties);
        modelPageLayout.setFlexGrow(1, model);
        modelPageLayout.add(modelPropertiesScroller, model);
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
                        renderer.loadModel(base64Model, null);
                    } catch (Exception ex) {
                        log.error("Chyba při zpracování modelu", ex);
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
                            renderer.loadModel(base64Model, base64Texture);
                        }
                    } catch (Exception ex) {
                        log.error("Chyba při zpracování modelu", ex);
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
                        renderer.loadModel(base64Model, base64Texture);
                    }
                } catch (Exception ex) {
                    log.error("Chyba při zpracování hlavní textury", ex);
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
}

