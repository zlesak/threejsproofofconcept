package cz.uhk.zlesak.threejslearningapp.views.listing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

@PageTitle("Kapitoly")
@Route("chapters")
@Scope("prototype")
public class ChapterListView extends Composite<VerticalLayout> {

    public ChapterListView() {

    }
}
