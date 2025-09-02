package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.BeforeLeaveEvent;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.utils.SpringContextUtils;

import java.util.function.Consumer;

/**
 * BeforeLeaveActionDialog Class - Provides a dialog to confirm the user's intention to leave a page
 * when there are unsaved changes, ensuring that the user is aware of potential data loss.
 */
public class BeforeLeaveActionDialog {
    /**
     * leave method - Displays a confirmation dialog when the user attempts to leave a page
     * @param event the event of leaving from the current page implementing BeforeLeaveEvent
     */
    public static void leave(BeforeLeaveEvent event) {
        CustomI18NProvider i18n = SpringContextUtils.getBean(CustomI18NProvider.class);
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle(i18n.getTranslation("beforeLeaveActionDialog.leave.info.assurance", UI.getCurrent().getLocale()));
        confirmDialog.add(i18n.getTranslation("beforeLeaveActionDialog.leave.info.unsavedChanges", UI.getCurrent().getLocale()));
        Button leaveButton = new Button(i18n.getTranslation("leave", UI.getCurrent().getLocale()), e -> {
            confirmDialog.close();
            postponed.proceed();
        });
        Button stayButton = new Button(i18n.getTranslation("stay", UI.getCurrent().getLocale()), e -> {
            postponed.cancel();
            confirmDialog.close();
        });
        confirmDialog.getFooter().add(leaveButton, stayButton);
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);
        confirmDialog.open();
    }

    /**
     * Overloaded leave method - Displays a confirmation dialog when the user attempts to leave a page when also
     * disposing of renderer is necessary passing a consumer to handle the confirmation action
     *
     * @param event the BeforeLeaveEvent
     * @param onConfirm a consumer receiving the postponed ContinueNavigationAction; must call proceed() or cancel()
     */
    public static void leave(BeforeLeaveEvent event, Consumer<BeforeLeaveEvent.ContinueNavigationAction> onConfirm) {
        CustomI18NProvider i18n = SpringContextUtils.getBean(CustomI18NProvider.class);
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle(i18n.getTranslation("beforeLeaveActionDialog.leave.info.assurance", UI.getCurrent().getLocale()));
        confirmDialog.add(i18n.getTranslation("beforeLeaveActionDialog.leave.info.unsavedChanges", UI.getCurrent().getLocale()));
        Button leaveButton = new Button(i18n.getTranslation("leave", UI.getCurrent().getLocale()), e -> {
            confirmDialog.close();
            try {
                if (onConfirm != null) {
                    onConfirm.accept(postponed);
                } else {
                    postponed.proceed();
                }
            } catch (Exception ex) {
                postponed.proceed();
            }
        });
        Button stayButton = new Button(i18n.getTranslation("stay", UI.getCurrent().getLocale()), e -> {
            postponed.cancel();
            confirmDialog.close();
        });
        confirmDialog.getFooter().add(leaveButton, stayButton);
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);
        confirmDialog.open();
    }
}
