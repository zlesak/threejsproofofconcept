package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public abstract class TextureScaffold  extends Composite<VerticalLayout> implements IView {
    protected final TextField textureName = new TextField("Název textury");
    protected final Image textureImage = new Image();
    protected final Checkbox isPrimary = new Checkbox("Jendá se o hlavní texturu?");
    protected final TextField modelId = new TextField("ID modelu, ke kterému se textura vztahuje");
    protected final VerticalLayout texture = new VerticalLayout();

    public TextureScaffold(ViewTypeEnum viewTypeEnum) {
        HorizontalLayout texturePageLayout = new HorizontalLayout();
        VerticalLayout textureProperties = new VerticalLayout();

        texturePageLayout.setWidthFull();
        texturePageLayout.setClassName("texturePageLayout");
        texturePageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        texturePageLayout.setFlexGrow(1, textureProperties);
        texturePageLayout.setFlexGrow(1, texture);
        texturePageLayout.add(textureProperties, texture);

        textureProperties.add(textureName, isPrimary, modelId);

        texture.add(textureImage);

        switch (viewTypeEnum){
            case CREATE -> {
                textureName.setLabel("Název textury");
                textureName.setPlaceholder("Zadejte název textury");
            }
            case EDIT -> {

            }
            case VIEW -> {
                textureName.setReadOnly(true);
                isPrimary.setVisible(false);
                modelId.setVisible(false);
            }
        }

        getContent().add(texturePageLayout);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

    }
}
