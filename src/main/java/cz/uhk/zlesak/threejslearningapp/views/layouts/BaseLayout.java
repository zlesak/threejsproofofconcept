package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import cz.uhk.zlesak.threejslearningapp.views.IView;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseLayout is an abstract base class for views that provides common functionality.
 * It extends Composite with a VerticalLayout as the content and implements the IView interface.
 * The class manages event registrations and ensures they are cleaned up when the view is detached.
 */
public abstract class BaseLayout extends Composite<VerticalLayout> implements IView {
    protected final List<Registration> registrations = new ArrayList<>();

    /**
     * Overridden onDetach function to clean up event registrations when the view is detached.
     * @param detachEvent the detach event
     */
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registrations.forEach(Registration::remove);
        registrations.clear();
    }
}
