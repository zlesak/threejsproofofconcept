package cz.uhk.zlesak.threejslearningapp.components.commonComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * Pagination component for navigating between pages of a result set.
 * Renders previous/next buttons and numbered page buttons with ellipsis for large page counts.
 */
public class PaginationComponent extends Nav implements I18nAware {

    /** WCAG 2.5.8 asks for at least 24 × 24 CSS px; touch guidance asks for more. */
    private static final String MIN_TARGET = "44px";

    private int currentPage;
    @Getter
    private int totalPages;
    private final Consumer<Integer> onPageChange;

    private final Button prevButton = new Button(new Icon(VaadinIcon.CHEVRON_LEFT));
    private final Button nextButton = new Button(new Icon(VaadinIcon.CHEVRON_RIGHT));
    private final HorizontalLayout layout = new HorizontalLayout();

    /**
     * Constructs the pagination component.
     *
     * @param page         zero-based current page index
     * @param limit        number of items per page
     * @param totalItems   total number of items in the result set
     * @param onPageChange callback invoked with the new zero-based page index on navigation
     */
    public PaginationComponent(int page, int limit, long totalItems, Consumer<Integer> onPageChange) {
        this.currentPage = page + 1;
        this.onPageChange = onPageChange;
        this.totalPages = (int) Math.ceil((double) totalItems / limit);

        // A run of numbered links is a navigation region; without a name a screen reader lists it as
        // one anonymous "navigation" among the others.
        getElement().setAttribute("aria-label", text("pagination.label"));

        layout.setSpacing(true);
        layout.setPadding(false);

        prevButton.setAriaLabel(text("pagination.previous"));
        nextButton.setAriaLabel(text("pagination.next"));
        sizeTarget(prevButton);
        sizeTarget(nextButton);

        prevButton.addClickListener(e -> goToPage(currentPage - 1));
        nextButton.addClickListener(e -> goToPage(currentPage + 1));

        updateButtons();
        updatePageNumbers();

        add(layout);
    }

    private void updatePageNumbers() {
        layout.removeAll();
        layout.add(prevButton);

        if (totalPages <= 1) {
            layout.add(createPageButton(1));
        } else if (totalPages == 2) {
            layout.add(createPageButton(1));
            layout.add(createPageButton(2));
        } else {
            layout.add(createPageButton(1));
            if (currentPage > 3) {
                layout.add(createEllipsis());
            }
            int start = Math.max(2, currentPage - 1);
            int end = Math.min(totalPages - 1, currentPage + 1);
            for (int i = start; i <= end; i++) {
                if (i == 1 || i == totalPages) continue;
                layout.add(createPageButton(i));
            }
            if (currentPage < totalPages - 2) {
                layout.add(createEllipsis());
            }
            layout.add(createPageButton(totalPages));
        }
        layout.add(nextButton);
    }

    private Div createEllipsis() {
        Div ellipsis = new Div();
        ellipsis.setText("...");
        ellipsis.getStyle().set("padding", "0 8px");
        ellipsis.getStyle().set("color", "var(--lumo-secondary-text-color)");
        // Three dots read aloud as "dot dot dot" tell the listener nothing.
        ellipsis.getElement().setAttribute("aria-hidden", "true");
        return ellipsis;
    }

    private Button createPageButton(int pageNum) {
        Button btn = new Button(String.valueOf(pageNum));
        btn.addClickListener(e -> goToPage(pageNum));
        sizeTarget(btn);
        if (pageNum == currentPage) {
            btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            // Marked as current rather than disabled. Disabling drops the button out of the tab order,
            // so a keyboard user tabbing through the pager skips over the page they are on and loses
            // their place in the sequence.
            btn.getElement().setAttribute("aria-current", "page");
            btn.setAriaLabel(text("pagination.current", pageNum));
        } else {
            btn.setAriaLabel(text("pagination.page", pageNum));
        }
        return btn;
    }

    private void sizeTarget(Button button) {
        button.getStyle()
                .set("min-width", MIN_TARGET)
                .set("min-height", MIN_TARGET);
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages || page == currentPage) return;
        this.currentPage = page;
        updateButtons();
        updatePageNumbers();
        if (onPageChange != null) {
            onPageChange.accept(currentPage - 1);
        }
    }

    private void updateButtons() {
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }
}
