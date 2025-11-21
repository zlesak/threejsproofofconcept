package cz.uhk.zlesak.threejslearningapp.components.dialogs.listDialogs;

import com.vaadin.flow.component.dialog.Dialog;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ListingLayout;
import lombok.Setter;

import java.util.function.Consumer;

public abstract class AbstractListDialog<Q> extends Dialog {
    @Setter
    private Consumer<Q> entitySelectedListener;
    private final ListingLayout<Q, ?> listView;

    public AbstractListDialog(ListingLayout<Q, ?> listView) {
        this.listView = listView;
        setWidth("800px");
        setHeight("600px");
        add(listView);
    }

    private void onEntitySelected(Q entity) {
        if (entitySelectedListener != null) {
            entitySelectedListener.accept(entity);
        }
        close();
    }

    @Override
    public void open() {
        this.setOpened(true);
        listView.setEntitySelectedListener(this::onEntitySelected);
        listView.listEntities();
    }
}
