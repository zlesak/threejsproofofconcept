package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import cz.uhk.zlesak.threejslearningapp.components.Selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.components.Selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForSelectRecord;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForSelectRecord;

import java.util.List;

/**
 * ModelDiv is a custom component that contains a layout for selecting textures and for ThreeJS renderer component
 *
 * @see ThreeJsComponent
 */
public class ModelDiv extends Div {
    private final TextureListingSelect textureListingSelect = new TextureListingSelect();
    private final TextureAreaSelect textureAreaSelect = new TextureAreaSelect();

    /**
     * Constructor for ModelDiv component.
     * It initializes the component with a horizontal layout containing texture selection combo boxes and a progress bar
     * for loading models, along with a ThreeJsComponent for rendering the model.
     *
     * @param progressBar the progress bar to indicate loading status
     * @param renderer    the ThreeJsComponent responsible for rendering the 3D model
     */
    public ModelDiv(ProgressBar progressBar, ThreeJsComponent renderer) {
        super();

        HorizontalLayout comboBoxHorizontalLayout = new HorizontalLayout();
        comboBoxHorizontalLayout.add(textureListingSelect, textureAreaSelect);
        comboBoxHorizontalLayout.setWidth("100%");
        add(comboBoxHorizontalLayout, progressBar, renderer);

        //Nastavení divId, DŮLEŽITÉ PRO THREEJS
        setId("modelDiv");
        setWidthFull();
        setHeightFull();
    }

    public void initializeTextureListingSelectData(List<TextureListingForSelectRecord> textureListingForSelect) {
        textureListingSelect.initializeTextureListingSelect(textureListingForSelect);
    }

    public void initializeTextureAreaSelect(List<TextureAreaForSelectRecord> textureAreaForSelect) {
        textureAreaSelect.initializeTextureAreaSelect(textureAreaForSelect);
    }
}
