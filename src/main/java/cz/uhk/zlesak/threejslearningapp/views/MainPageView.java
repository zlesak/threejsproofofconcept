package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.commonComponents.DividerComponent;
import cz.uhk.zlesak.threejslearningapp.views.abstractViews.IView;
import cz.uhk.zlesak.threejslearningapp.views.chapter.ChapterListingView;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListingView;

/**
 * Main page view of the application.
 * This view is accessible at the root route ("/").
 * It provides an overview of the application features and navigation options.
 */
@Route("")
@Tag("main-page-view")
@AnonymousAllowed
public class MainPageView extends Composite<VerticalLayout> implements IView {
    private static final String TRANSPARENT_GIF_PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";
    /**
     * Constructor for MainPageView.
     * Initializes the main layout and adds sections to the page.
     */
    public MainPageView() {
        VerticalLayout mainLayout = getContent();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainLayout.getStyle().set("margin", "0 auto");
        mainLayout.addAttachListener(event -> initGifLazyLoading());
        mainLayout.add(
                createHeroSection(),
                new DividerComponent(),
                createAboutSection(),
                new DividerComponent(),
                createFeaturesSection(),
                new DividerComponent(),
                createShowcaseSection(),
                new DividerComponent(),
                createCollaborationSection(),
                createFooterSection()
        );
    }

    /**
     * Creates the hero section of the main page.
     */
    private HorizontalLayout createHeroSection() {
        HorizontalLayout section = new HorizontalLayout();
        section.addClassName("main-hero-section");
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        section.setWidthFull();
        section.setMinHeight("calc(100vh - 100px)");

        Div logoWrapper = new Div();
        logoWrapper.addClassName("main-hero-logo");
        logoWrapper.setWidth("50%");
        logoWrapper.setHeightFull();
        logoWrapper.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER, LumoUtility.Display.FLEX);

        // Decorative: the application's name is right beside it as the H1, so describing the logo would
        // only make a screen reader say "MISH" twice.
        Image logo = new Image("/img/MISH_big.png", "");
        logo.setMaxWidth("90%");
        logo.setMaxHeight("80vh");
        logo.getStyle().set("object-fit", "contain");

        logoWrapper.add(logo);

        VerticalLayout textContent = new VerticalLayout();
        textContent.addClassName("main-hero-text");
        textContent.setWidth("50%");
        textContent.setSpacing(false);
        textContent.setAlignItems(FlexComponent.Alignment.START);
        textContent.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);


        H1 title = new H1(text("applicationTitle"));
        title.addClassNames(LumoUtility.Margin.Bottom.SMALL, LumoUtility.FontSize.XXXLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.LineHeight.MEDIUM);

        // A slogan, not a section: it introduces nothing that follows it, so an H2 here put an empty
        // rung on the heading ladder a screen reader user climbs.
        Paragraph subtitle = new Paragraph(text("welcomeMessage"));
        subtitle.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.FontSize.XLARGE, LumoUtility.TextColor.SECONDARY);

        Paragraph description = new Paragraph(text("description"));
        description.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.LARGE);
        applyReadableMeasure(description);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addClassName("main-hero-cta");
        Button startBtn = new Button(text("cta.start"), new Icon(VaadinIcon.OPEN_BOOK));
        startBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        startBtn.addClickListener(e -> UI.getCurrent().navigate(ChapterListingView.class));

        Button modelsBtn = new Button(text("cta.models"), new Icon(VaadinIcon.CUBES));
        modelsBtn.addThemeVariants(ButtonVariant.LUMO_LARGE);
        // A pale grey surface on a white page gave the control a boundary of 1.1 : 1 — for practical
        // purposes no boundary at all. The border is what makes it recognisable as a button.
        modelsBtn.getStyle().set("border", "2px solid #A3232A");
        modelsBtn.addClickListener(e -> UI.getCurrent().navigate(ModelListingView.class));

        buttons.add(startBtn, modelsBtn);

        textContent.add(title, subtitle, description, buttons);

        section.add(logoWrapper, textContent);
        return section;
    }

    /**
     * Creates the features section of the main page.
     * @return The VerticalLayout containing the features section.
     */
    private VerticalLayout createFeaturesSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("main-section-features");
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        section.setWidth("80%");

        H2 title = new H2(text("features.title"));
        title.addClassNames(LumoUtility.Margin.Bottom.XLARGE);

        FlexLayout cardsLayout = new FlexLayout();
        cardsLayout.addClassName("main-features-grid");
        cardsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cardsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        cardsLayout.getStyle().set("gap", "var(--lumo-space-xl)");
        cardsLayout.setWidthFull();
        cardsLayout.setMaxWidth("1200px");

        cardsLayout.add(
                createFeatureCard(VaadinIcon.CUBE, "features.models.title", "features.models.desc"),
                createFeatureCard(VaadinIcon.BOOK, "features.chapters.title", "features.chapters.desc"),
                createFeatureCard(VaadinIcon.QUESTION, "features.quizzes.title", "features.quizzes.desc"),
                createFeatureCard(VaadinIcon.LAPTOP, "features.platform.title", "features.platform.desc")
        );

        section.add(title, cardsLayout);
        return section;
    }

    private VerticalLayout createFeatureCard(VaadinIcon icon, String titleKey, String descKey) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("main-feature-card");
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setWidth("250px");
        card.addClassNames(LumoUtility.TextAlignment.CENTER);

        Icon i = icon.create();
        i.setSize("48px");
        i.addClassNames(LumoUtility.TextColor.PRIMARY, LumoUtility.Margin.Bottom.MEDIUM);

        H3 title = new H3(text(titleKey));
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.SMALL);

        Paragraph desc = new Paragraph(text(descKey));
        desc.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        card.add(i, title, desc);
        return card;
    }

    /**
     * Creates the showcase section of the main page.
     * @return The VerticalLayout containing the showcase section.
     */
    private VerticalLayout createShowcaseSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("main-section-showcase");
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        section.setWidth("80%");

        H2 title = new H2(text("showcase.title"));
        Paragraph desc = new Paragraph(text("showcase.desc"));
        title.addClassNames(LumoUtility.Margin.Bottom.LARGE);

        FlexLayout showcaseGrid = new FlexLayout();
        showcaseGrid.addClassName("main-showcase-grid");
        showcaseGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        showcaseGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        showcaseGrid.getStyle().set("gap", "var(--lumo-space-l)");
        showcaseGrid.setWidthFull();
        showcaseGrid.setMaxWidth("1200px");

        showcaseGrid.add(createGifPlaceholder("showcase.gif1.title", "/img/modelgif.gif"));
        showcaseGrid.add(createGifPlaceholder("showcase.gif2.title", "/img/kapitolagif.gif"));
        showcaseGrid.add(createGifPlaceholder("showcase.gif3.title", "/img/quizgif.gif"));

        section.add(title, desc, showcaseGrid);
        return section;
    }

    /**
     * Creates a showcase item with a GIF/Image and a title.
     * @param titleKey The key for the title text.
     * @param gifPath The path to the GIF image.
     * @return The VerticalLayout containing the GIF/Image and title.
     */
    private VerticalLayout createGifPlaceholder(String titleKey, String gifPath) {
        VerticalLayout container = new VerticalLayout();
        container.addClassName("main-showcase-item");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        container.setWidthFull();
        container.setPadding(false);

        Image gifImage = new Image(TRANSPARENT_GIF_PLACEHOLDER, text(titleKey));
        gifImage.addClassName("main-showcase-gif");
        gifImage.getElement().setAttribute("data-gif-src", gifPath);
        gifImage.getElement().setAttribute("decoding", "async");
        gifImage.getElement().setAttribute("fetchpriority", "low");
        gifImage.setWidthFull();
        gifImage.setHeight("400px");
        gifImage.getStyle()
                .set("object-fit", "contain")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("background", "var(--lumo-contrast-5pct)");

        // An H3 under the section's H2. It was an H4, which skipped a level.
        H3 title = new H3(text(titleKey));
        title.addClassNames(LumoUtility.Margin.Top.SMALL);

        // The animations ran in a loop, for far longer than five seconds, with no way of stopping them.
        // Nothing plays until this is pressed, and pressing it again freezes the frame on screen.
        Button playToggle = new Button(text("showcase.play"), new Icon(VaadinIcon.PLAY));
        playToggle.addClassName("main-showcase-play");
        playToggle.getElement().setAttribute("data-play-label", text("showcase.play"));
        playToggle.getElement().setAttribute("data-stop-label", text("showcase.stop"));
        playToggle.getElement().setAttribute("aria-pressed", "false");
        playToggle.addClassNames(LumoUtility.Margin.Top.XSMALL);

        container.add(gifImage, title, playToggle);
        return container;
    }

    /**
     * Wires the play controls and preloads the animations without starting them.
     *
     * <p>The preload goes into the browser cache rather than into the visible element, so scrolling the
     * section into view no longer sets an animation running. Stopping draws the frame that is on screen
     * onto a canvas and shows that instead, which leaves a still picture rather than an empty box.
     */
    private void initGifLazyLoading() {
        getContent().getElement().executeJs(
                """
                const root = this;
                const blank = $0;
                const items = root.querySelectorAll('.main-showcase-item:not([data-gif-bound])');
                if (!items.length) {
                  return;
                }

                const preload = (src) => {
                  const warm = new Image();
                  warm.decoding = 'async';
                  warm.src = src;
                };

                const freeze = (img) => {
                  try {
                    const canvas = document.createElement('canvas');
                    canvas.width = img.naturalWidth || img.clientWidth;
                    canvas.height = img.naturalHeight || img.clientHeight;
                    if (!canvas.width || !canvas.height) {
                      return null;
                    }
                    canvas.getContext('2d').drawImage(img, 0, 0);
                    return canvas.toDataURL('image/png');
                  } catch (e) {
                    return null;
                  }
                };

                items.forEach((item) => {
                  const img = item.querySelector('img[data-gif-src]');
                  const button = item.querySelector('.main-showcase-play');
                  if (!img || !button) {
                    return;
                  }
                  item.setAttribute('data-gif-bound', 'true');

                  const src = img.getAttribute('data-gif-src');
                  let playing = false;
                  let stillFrame = null;

                  const setLabel = () => {
                    const label = playing
                      ? button.getAttribute('data-stop-label')
                      : button.getAttribute('data-play-label');
                    button.setAttribute('aria-pressed', playing ? 'true' : 'false');
                    const textNode = Array.from(button.childNodes)
                      .find((node) => node.nodeType === Node.TEXT_NODE);
                    if (textNode) {
                      textNode.textContent = label;
                    } else {
                      button.setAttribute('aria-label', label);
                    }
                  };

                  button.addEventListener('click', () => {
                    if (playing) {
                      stillFrame = freeze(img) || stillFrame;
                      img.src = stillFrame || blank;
                      playing = false;
                    } else {
                      img.src = src;
                      playing = true;
                    }
                    setLabel();
                  });

                  if ('IntersectionObserver' in window) {
                    const observer = new IntersectionObserver((entries) => {
                      entries.forEach((entry) => {
                        if (entry.isIntersecting || entry.intersectionRatio > 0) {
                          preload(src);
                          observer.unobserve(entry.target);
                        }
                      });
                    }, { root: null, rootMargin: '300px 0px', threshold: 0.01 });
                    observer.observe(item);
                  } else {
                    preload(src);
                  }
                });
                """,
                TRANSPARENT_GIF_PLACEHOLDER
        );
    }

    /**
     * Creates the about and collaboration section of the main page.
     * @return The VerticalLayout containing the about and collaboration section.
     */
    private VerticalLayout createAboutSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("main-section-about");
        section.setWidth("80%");
        section.setAlignItems(FlexComponent.Alignment.CENTER);

        H2 aboutTitle = new H2(text("about.title"));
        aboutTitle.addClassNames(LumoUtility.Margin.Top.NONE);

        Paragraph aboutDesc = new Paragraph(text("about.description"));
        aboutDesc.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);
        aboutDesc.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
        applyReadableMeasure(aboutDesc);

        VerticalLayout aboutCol = new VerticalLayout(aboutTitle, aboutDesc);
        aboutCol.setPadding(false);
        aboutCol.setMinWidth("300px");
        aboutCol.setWidthFull();
        aboutCol.setAlignItems(FlexComponent.Alignment.CENTER);
        section.add(aboutCol);
        return section;
    }
    private VerticalLayout createCollaborationSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassName("main-section-collaboration");
        section.setWidth("80%");
        section.setAlignItems(FlexComponent.Alignment.CENTER);

        // A section of the page like the others, so an H2 like the others.
        H2 collabTitle = new H2(text("collaboration.title"));
        collabTitle.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.Bottom.NONE);

        Paragraph collabDesc = new Paragraph(text("collaboration.description"));
        collabDesc.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
        applyReadableMeasure(collabDesc);

        VerticalLayout collabCol = new VerticalLayout(collabTitle, collabDesc);
        collabCol.setPadding(false);
        collabCol.setMinWidth("300px");
        collabCol.setWidthFull();
        collabCol.setAlignItems(FlexComponent.Alignment.CENTER);


        HorizontalLayout logosLayout = new HorizontalLayout();
        logosLayout.addClassName("main-collab-logos");
        logosLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        logosLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logosLayout.setWidthFull();
        logosLayout.addClassNames(LumoUtility.Gap.XLARGE, LumoUtility.FlexWrap.WRAP);

        // Named in full. "UHK Logo" told the listener the shape of the thing rather than whose it is,
        // and the abbreviation means nothing to anyone outside the two faculties.
        Div uhkLogoWrapper = creteLogoWrapper("/img/fim-uhk-abb_xs_rgb.png", "/img/fim-uhk-abb_xs_rgb-neg.png",
                text("collaboration.logo.fim"));
        Div lfhkLogoWrapper = creteLogoWrapper("/img/LFHK-337-version1-logo_lfhk.png", "/img/LFHK-337-version1-logo_lfhk_bila.png",
                text("collaboration.logo.lfhk"));

        logosLayout.add(uhkLogoWrapper, lfhkLogoWrapper);

        section.add(collabCol, logosLayout);
        return section;
    }

    private Div creteLogoWrapper(String light, String dark, String alt) {
        Div logoWrapper = new Div();

        Image logoLight = new Image(light, alt);
        logoLight.setHeight("150px");
        logoLight.addClassName("logo-light");
        logoLight.getStyle().set("object-fit", "contain");

        Image logoDark = new Image(dark, alt);
        logoDark.setHeight("150px");
        logoDark.addClassName("logo-dark");
        logoDark.getStyle().set("object-fit", "contain");
        logoWrapper.add(logoLight, logoDark);
        return logoWrapper;
    }

    private Footer createFooterSection() {
        Footer footer = new Footer();
        footer.setWidthFull();
        footer.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.MEDIUM);
        int currentYear = java.time.Year.now().getValue();
        footer.add(new Span("© " + currentYear + " MISH | " + text("footer.rights")));

        // Required by the European Accessibility Act. The statement's wording is the operator's to
        // write; it belongs in the documentation, which is where this points.
        Anchor statement = new Anchor("/documentation", text("footer.accessibility"));
        statement.addClassNames(LumoUtility.Display.BLOCK, LumoUtility.Margin.Top.XSMALL);
        footer.add(statement);
        return footer;
    }

    /**
     * Caps a paragraph at a comfortable measure and leaves it aligned to the start.
     *
     * <p>The three long paragraphs were justified. Justification stretches the word spacing of every
     * line differently, and the resulting "rivers" of white running down the block are a documented
     * obstacle for readers with dyslexia — as is a line so long the eye loses its place returning to
     * the start of the next one.
     *
     * @param paragraph the paragraph to constrain
     */
    private void applyReadableMeasure(Paragraph paragraph) {
        paragraph.getStyle()
                .set("max-width", "34em")
                .set("text-align", "start");
    }

    /**
     * Gets the title of the page.
     *
     * @return The page title as a string.
     */
    @Override
    public String getPageTitle() {
        return text("page.title.mainPageView");
    }
}
