package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.TextureAreaComboBox;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.TextureListingComboBox;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForComboBoxRecord;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForComboBoxRecord;

import java.util.List;

public class ModelDiv extends Div {
    private final TextureListingComboBox textureListingComboBox = new TextureListingComboBox();
    private final TextureAreaComboBox textureAreaComboBox = new TextureAreaComboBox();

    public ModelDiv(ProgressBar progressBar, ThreeJsComponent renderer) {
        super();

        HorizontalLayout comboBoxHorizontalLayout = new HorizontalLayout();
        comboBoxHorizontalLayout.add(textureListingComboBox, textureAreaComboBox);
        comboBoxHorizontalLayout.setWidth("100%");
        add(comboBoxHorizontalLayout, progressBar, renderer);

        //Nastavení divId, DŮLEŽITÉ PRO THREEJS
        setId("modelDiv");
        setWidthFull();
        setHeightFull();
    }

    public void setModel(String model) {

    }

    public void initializeTextureListingComboBoxData(List<TextureListingForComboBoxRecord> textureListingForComboBox) {
        textureListingComboBox.initializeTextureListingComboBox(textureListingForComboBox);
    }
    public void initializeTextureAreaComboBox(List<TextureAreaForComboBoxRecord> textureAreaForComboBox) {
        textureAreaComboBox.initializeTextureAreaComboBox(textureAreaForComboBox);
    }
}
