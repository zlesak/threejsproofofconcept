package cz.uhk.zlesak.threejslearningapp.components.editors;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import cz.uhk.zlesak.threejslearningapp.events.editor.MarkdownModeToggleEvent;
import cz.uhk.zlesak.threejslearningapp.events.editor.MarkdownValueChangedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A Markdown editor component that listens for markdown mode toggle events and updates its visibility and content accordingly.
 * It fires markdown value changed events when switching out of markdown mode.
 */
public class MarkdownEditor extends com.flowingcode.vaadin.addons.markdown.MarkdownEditor {
    private final List<Registration> registrations = new ArrayList<>();

    public MarkdownEditor() {
        super();
        setSizeFull();
        setPlaceholder("Začněte tvořit pomocí syntaxe markdown...");
        setVisible(false);
    }
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                MarkdownModeToggleEvent.class,
                event -> {
                    if (!event.isMarkdownMode()) {
                        ComponentUtil.fireEvent(UI.getCurrent(), new MarkdownValueChangedEvent(UI.getCurrent(), getValue(), false));
                        setVisible(false);

                    } else {
                        setVisible(true);
                    }
                }
        ));

        registrations.add(
                ComponentUtil.addListener(
                        attachEvent.getUI(),
                        MarkdownValueChangedEvent.class,
                        event -> {
                            if(event.isMarkdownMode()) {
                                setValue(event.getMarkdownValue());
                            }
                        }
                )
        );
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registrations.forEach(Registration::remove);
        registrations.clear();
    }
}
