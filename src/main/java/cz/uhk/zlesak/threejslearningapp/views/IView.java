package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasDynamicTitle;

public interface IView extends BeforeLeaveObserver, BeforeEnterObserver, HasDynamicTitle {
}
