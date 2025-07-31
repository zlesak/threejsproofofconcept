package cz.uhk.zlesak.threejslearningapp.views.listing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

@PageTitle("Modely")
@Route("models")
@Scope("prototype")
public class ModelListView extends Composite<VerticalLayout> {
    public ModelListView(){}
}
