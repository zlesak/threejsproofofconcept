package cz.uhk.zlesak.threejslearningapp.components.inputs.files;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import cz.uhk.zlesak.threejslearningapp.components.editors.EditorJs;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class MarkdownFileUpload extends FileUpload implements I18nAware {
    public MarkdownFileUpload(EditorJs editorjs, MarkdownEditor mdEditor, Button mdUploadedButton) {
        super(List.of(".md", "text/markdown", "text/plain"), true, false, false);
        setUploadButton(new Button(text("markdownUploadButton.label"), new Icon(VaadinIcon.UPLOAD)));
        setUploadListener((fileName, uploadedMultipartFile) -> {
            try {
                String md = new String(uploadedMultipartFile.getBytes(), StandardCharsets.UTF_8);
                editorjs.loadMarkdown(md);
                mdEditor.setValue(md);
                mdUploadedButton.setText(fileName);
                mdUploadedButton.setIcon(new Icon(VaadinIcon.CHECK));
                mdUploadedButton.setEnabled(false);
                mdUploadedButton.setVisible(true);
                setVisible(false);
            } catch (Exception ex) {
                log.error("Chyba při načítání MD souboru: {}", ex.getMessage(), ex);
                new ErrorNotification("Nepodařilo se načíst MD soubor: " + ex.getMessage(), 5000);
            }
        });
        addFileRejectedListener(e -> new ErrorNotification("Nahrávání selhalo: " + e.getErrorMessage(), 5000));
    }
}
