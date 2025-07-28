package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IView extends BeforeLeaveObserver, BeforeEnterObserver {
    Logger logger = LoggerFactory.getLogger(IView.class);
}
