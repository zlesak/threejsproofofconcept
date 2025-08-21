package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasDynamicTitle;

/**
 * Interface representing a view in the application.
 * It extends BeforeLeaveObserver and BeforeEnterObserver to handle navigation events.
 * + HasDynamicTitle to support dynamic titles for the views.
 */
public interface IView extends BeforeLeaveObserver, BeforeEnterObserver, HasDynamicTitle, AfterNavigationObserver {
}
