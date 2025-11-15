package cz.uhk.zlesak.threejslearningapp.views.quizes;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.views.layouts.QuizLayout;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("quizes")
@Scope("prototype")
@Tag("quizes-listing")
@PermitAll
public class QuizListView extends QuizLayout {
    /**
     * @return
     */
    @Override
    public String getPageTitle() {
        return text("page.title.quizListView");
    }


    /**
     * Called before entering the view.
     * Default implementation does nothing.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
    }

    /**
     * Called before leaving the view.
     * Default implementation does nothing.
     *
     * @param event before leave event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        super.beforeLeave(event);
    }

    /**
     * Called after navigation to the view is complete.
     * Default implementation does nothing.
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        super.afterNavigation(event);
    }
}
