package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;

/**
 * ModelDiv is a custom Div component that contains a ThreeJsComponent for rendering 3D models,
 * along with an overlay progress bar and description for loading actions coming back from the ThreeJsComponent.
 *
 * @see ThreeJsComponent
 */
public class ModelDiv extends Div {
    private final ProgressBar overlayProgressBar;
    private final Div overlayBackground;
    private final Span actionDescription;

    /**
     * Constructor for ModelDiv component.
     */
    public ModelDiv(ThreeJsComponent renderer) {
        super();

        //Nastavení divId, DŮLEŽITÉ PRO THREEJS
        setId("modelDiv");

        getStyle().set("flex", "1 1 auto");
        getStyle().set("height", "100%");
        getStyle().set("width", "100%");
        getStyle().set("max-height", "100%");
        getStyle().set("min-height", "0");
        getStyle().set("position", "relative");
        getStyle().remove("z-index");

        overlayBackground = new Div();
        overlayBackground.setVisible(false);
        overlayBackground.getStyle().set("position", "absolute");
        overlayBackground.getStyle().set("top", "0");
        overlayBackground.getStyle().set("left", "0");
        overlayBackground.getStyle().set("width", "100%");
        overlayBackground.getStyle().set("height", "100%");
        overlayBackground.getStyle().set("background", "rgba(0,0,0,0.3)");
        overlayBackground.getStyle().set("z-index", "10");

        overlayProgressBar = new ProgressBar();
        overlayProgressBar.setIndeterminate(true);
        overlayProgressBar.setVisible(false);
        overlayProgressBar.getStyle().set("position", "absolute");
        overlayProgressBar.getStyle().set("top", "50%");
        overlayProgressBar.getStyle().set("left", "50%");
        overlayProgressBar.getStyle().set("transform", "translate(-50%, -50%)");
        overlayProgressBar.getStyle().set("z-index", "11");
        overlayProgressBar.setWidth("300px");

        actionDescription  = new Span();
        actionDescription.getStyle().set("position", "absolute");
        actionDescription.getStyle().set("top", "55%");
        actionDescription.getStyle().set("left", "50%");
        actionDescription.getStyle().set("transform", "translate(-50%, -50%)");
        actionDescription.getStyle().set("z-index", "11");
        actionDescription.setWidth("300px");

        removeAll();
        add(renderer, overlayBackground, overlayProgressBar, actionDescription);
        renderer.addThreeJsDoingActionsListener(e -> {
            actionDescription.setText(e.getDescription());
            showOverlayProgressBar();
        });
        renderer.addThreeJsFinishedActionsListener(e -> hideOverlayProgressBar());
    }

    private void showOverlayProgressBar() {
        overlayBackground.setVisible(true);
        overlayProgressBar.setVisible(true);
        actionDescription.setVisible(true);
    }

    private void hideOverlayProgressBar() {
        overlayBackground.setVisible(false);
        overlayProgressBar.setVisible(false);
        actionDescription.setVisible(false);
    }
}
