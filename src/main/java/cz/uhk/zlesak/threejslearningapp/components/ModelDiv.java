package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;

/**
 * ModelDiv is a custom component that contains a layout for selecting textures and for ThreeJS renderer component
 *
 * @see ThreeJsComponent
 */
public class ModelDiv extends Div {

    private final ProgressBar overlayProgressBar;
    private final Div overlayBackground;
    private final Span actionDescription;


    /**
     * Constructor for ModelDiv component.
     * It initializes the component with a horizontal layout containing texture selection combo boxes and a progress bar
     * for loading models, along with a ThreeJsComponent for rendering the model.
     *
     * @param progressBar the progress bar to indicate loading status
     * @param renderer    the ThreeJsComponent responsible for rendering the 3D model
     */
    public ModelDiv(ProgressBar progressBar, ThreeJsComponent renderer) {
        super();
        add(progressBar, renderer);


        overlayBackground = new Div();
        overlayBackground.setVisible(false);
        overlayBackground.getStyle().set("position", "absolute");
        overlayBackground.getStyle().set("top", "0");
        overlayBackground.getStyle().set("left", "0");
        overlayBackground.getStyle().set("width", "100%");
        overlayBackground.getStyle().set("height", "100%");
        overlayBackground.getStyle().set("background", "rgba(0,0,0,0.3)");
        overlayBackground.getStyle().set("z-index", "10");
        add(overlayBackground);

        overlayProgressBar = new ProgressBar();
        overlayProgressBar.setIndeterminate(true);
        overlayProgressBar.setVisible(false);
        overlayProgressBar.getStyle().set("position", "absolute");
        overlayProgressBar.getStyle().set("top", "50%");
        overlayProgressBar.getStyle().set("left", "50%");
        overlayProgressBar.getStyle().set("transform", "translate(-50%, -50%)");
        overlayProgressBar.getStyle().set("z-index", "11");
        overlayProgressBar.setWidth("300px");
        add(overlayProgressBar);

        actionDescription  = new Span();
        actionDescription.getStyle().set("position", "absolute");
        actionDescription.getStyle().set("top", "55%");
        actionDescription.getStyle().set("left", "50%");
        actionDescription.getStyle().set("transform", "translate(-50%, -50%)");
        actionDescription.getStyle().set("z-index", "11");
        actionDescription.setWidth("300px");
        add(actionDescription);

        //Nastavení divId, DŮLEŽITÉ PRO THREEJS
        setId("modelDiv");
        setWidthFull();
        setHeightFull();
        getStyle().set("position", "relative");
        getStyle().remove("z-index"); // ModelDiv nemá z-index

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
