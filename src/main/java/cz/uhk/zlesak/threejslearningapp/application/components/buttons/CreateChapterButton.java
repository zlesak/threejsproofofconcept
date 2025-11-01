package cz.uhk.zlesak.threejslearningapp.application.components.buttons;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import cz.uhk.zlesak.threejslearningapp.application.events.CreateChapterEvent;
import cz.uhk.zlesak.threejslearningapp.application.i18n.I18nAware;
import lombok.extern.slf4j.Slf4j;

/**
 * A button component for creating a new chapter.
 * When clicked, it fires a CreateChapterEvent to notify listeners.
 */
@Slf4j
public class CreateChapterButton extends Button implements I18nAware {
    public CreateChapterButton() {
        super();
        setText(text("createChapterButton.label"));
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClickListener(e -> ComponentUtil.fireEvent(UI.getCurrent(), new CreateChapterEvent(UI.getCurrent())));
    }
}
