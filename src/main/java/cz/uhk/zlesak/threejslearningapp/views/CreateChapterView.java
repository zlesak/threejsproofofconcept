package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.EditorJs;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import org.springframework.context.ApplicationContextException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Vytvořit kapitolu")
@Route("createChapter")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("create-chapter")
public class CreateChapterView extends Composite<VerticalLayout> {

    public CreateChapterView(ChapterController chapterController) {
/// chapter name text field
        TextField header = new TextField("Název kapitoly");
        header.setMaxLength(255);
        header.setRequired(true);
        header.setRequiredIndicatorVisible(true);
        header.setWidthFull();
/// content field
        EditorJs editorJs = new EditorJs();
/// model upload
        UploadComponent upload = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"));
/// create button
        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();
///  createChapterButton button listener to submit the chapter with validations
        createChapterButton.addClickListener(e -> {
            String name = header.getValue().trim();

            if (name.isEmpty()) {
                Notification.show("Vyplňte název kapitoly.");
                return;
            }

            editorJs.getData().whenComplete((body, error) -> {
                if (error != null) {
                    Notification.show("Chyba při získávání obsahu: " + error.getMessage());
                    return;
                }

                if (body.isEmpty()) {
                    Notification.show("Přidejte nějaký obsah kapitoly.");
                    return;
                }

                try {
                    ChapterEntity created = chapterController.createChapter(name, body);
                    if (created != null) {

                        UI.getCurrent().navigate("chapter/" + created.getChapterEntityId());
                    } else {
                        throw new ApplicationContextException("Kapitola nebyla uložena, zkuste to znovu později.");
                    }
                } catch (Exception ex) {
                    Notification.show("Chyba při vytváření kapitoly: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                }
            });
        });
/// Layout setup
        VerticalLayout layoutColumn1 = new VerticalLayout();
        layoutColumn1.setWidth("80vw");
        layoutColumn1.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        layoutColumn1.add(header, editorJs, upload, createChapterButton);

        VerticalLayout centerWrapper = new VerticalLayout();
        centerWrapper.setWidthFull();
        centerWrapper.setHeightFull();
        centerWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        centerWrapper.add(layoutColumn1);
        /// scroller for dynamic addition of chapter components added by user, that resolve in dynamic height of the page
        Scroller scroller = new Scroller(centerWrapper);
        scroller.setSizeFull();

        getContent().setSizeFull();
        getContent().removeAll();
        getContent().add(scroller);
    }
}
