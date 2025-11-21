package cz.uhk.zlesak.threejslearningapp.components.scrollers;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import cz.uhk.zlesak.threejslearningapp.components.dialogs.listDialogs.ModelListDialog;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListingView;
import org.springframework.context.ApplicationContextException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A scroller component that contains selects for choosing 3D models for the main chapter and its sub-chapters.
 * Each select is paired with a button to open a dialog for selecting already created models.
 * The component allows dynamic initialization of selects based on existing sub-chapters.
 *
 */
public class ModelsSelectScroller extends Scroller implements I18nAware {
    private Select<QuickModelEntity> mainModelSelect;
    private final Map<String, HorizontalLayout> otherModelsHorizontalLayouts = new HashMap<>();
    private final VerticalLayout scrollerLayout;
    private Consumer<Map<String, QuickModelEntity>> modelSelectConsumer;

    public ModelsSelectScroller() {
        super(new VerticalLayout(), ScrollDirection.VERTICAL);
        this.scrollerLayout = (VerticalLayout) getContent();
        setSizeFull();
    }

    /**
     * Get model select component.
     *
     * @param label Label for the select component.
     * @return Select component for QuickModelEntity in deactivated state as the model is selected via dialog.
     */
    private Select<QuickModelEntity> getModelSelect(String label, String id) {
        Select<QuickModelEntity> modelSelect = new Select<>();
        modelSelect.setHelperText(label);
        modelSelect.getElement().setAttribute("block-id", id);
        modelSelect.setItemLabelGenerator(entity -> entity != null ? entity.getModel().getName() : "");
        modelSelect.setWidthFull();
        modelSelect.setReadOnly(true);
        return modelSelect;
    }

    /**
     * Set consumer to be called when a model is selected on any of the selects.
     *
     * @param onSelect Consumer to be called with the selected QuickModelEntity.
     */
    public void setModelSelectedConsumer(Consumer<Map<String, QuickModelEntity>> onSelect) {
        modelSelectConsumer = onSelect;
    }

    /**
     * Initialize selects for main model and other models for sub-chapters.
     *
     * @param subChapterForSelectRecords Map of sub-chapter IDs to their names for which model selects should be created.
     *                                   Records are returned from backend to ensure only existing sub-chapters have selects.
     */
    public void initSelects(Map<String, String> subChapterForSelectRecords) {
        if (this.mainModelSelect == null) {
            modelSelectHorizontalLayout(text("modelSelect.main.caption"), "", true);
        }

        otherModelsHorizontalLayouts.keySet().removeIf(id -> {
            if (!subChapterForSelectRecords.containsKey(id)) {
                this.scrollerLayout.remove(otherModelsHorizontalLayouts.get(id));
                return true;
            }
            return false;
        });

        subChapterForSelectRecords.forEach((id, text) -> {
            if (!otherModelsHorizontalLayouts.containsKey(id)) {
                modelSelectHorizontalLayout(text("modelSelect.other.caption") + text, id, false);
            }
        });
    }

    /**
     * Create horizontal layout with model select and button to choose already created model.
     *
     * @param label label for select component.
     * @param id    chapter header block ID for which the model is selected.
     * @param main  whether this is the main model select or sub-chapter model select.
     */
    private void modelSelectHorizontalLayout(String label, String id, boolean main) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        Select<QuickModelEntity> select = getModelSelect(label, id);

        Button alreadyCreatedModelButton = getChooseAlreadyCreatedModelButton(select);
        horizontalLayout.add(select, alreadyCreatedModelButton);

        if (main) {
            this.mainModelSelect = select;
        } else {
            horizontalLayout.setId("select-models-tab-piece-" + id);
            otherModelsHorizontalLayouts.putIfAbsent(id, horizontalLayout);
        }
        scrollerLayout.add(horizontalLayout);
    }

    /**
     * Get button to choose already created model.
     * Using ModelListDialog to select model via paged model selector.
     *
     * @param modelSelect Select component to set the selected model to.
     * @return Button to open ModelListDialog.
     */
    private Button getChooseAlreadyCreatedModelButton(Select<QuickModelEntity> modelSelect) {
        Button chooseAlreadyCreatedModelButton = new Button(text("modelSelectButton.label"));

        ModelListDialog modelListDialog = new ModelListDialog(new ModelListingView());
        modelListDialog.setEntitySelectedListener(entity -> {
            modelSelect.setItems(entity);
            modelSelect.setValue(entity);
            if (modelSelect.getValue() != null) {
                modelSelectConsumer.accept(getAllModelsMappedToChapterHeaderBlockId());
            }
        });
        chooseAlreadyCreatedModelButton.addClickListener(e -> modelListDialog.open());

        return chooseAlreadyCreatedModelButton;
    }

    /**
     * Get all selected models mapped to their chapter header block IDs.
     * main model is mapped to "main" key, not to any chapter header block ID.
     *
     * @return Map of chapter header block IDs to selected QuickModelEntity instances.
     * @throws ApplicationContextException if the main model is not selected or the main model select has not been initialized yet.
     */
    public Map<String, QuickModelEntity> getAllModelsMappedToChapterHeaderBlockId() throws ApplicationContextException {

        if (mainModelSelect == null || mainModelSelect.getValue() == null) {
            throw new ApplicationContextException("Hlavní model není vybrán!");
        }

        Map<String, QuickModelEntity> models = new HashMap<>();
        for (HorizontalLayout layout : otherModelsHorizontalLayouts.values()) {
            @SuppressWarnings("unchecked") Select<QuickModelEntity> select = ((Select<QuickModelEntity>) layout.getComponentAt(0));
            QuickModelEntity selected = select.getValue();
            if (selected != null) {
                models.put(select.getElement().getAttribute("block-id"), selected);
            }
        }

        models.put("main", mainModelSelect.getValue());
        return models;
    }
}
