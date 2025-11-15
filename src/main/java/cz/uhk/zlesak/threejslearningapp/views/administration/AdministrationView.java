package cz.uhk.zlesak.threejslearningapp.views.administration;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.services.ChapterService;
import cz.uhk.zlesak.threejslearningapp.services.ModelService;
import cz.uhk.zlesak.threejslearningapp.services.QuizService;
import cz.uhk.zlesak.threejslearningapp.views.chapter.ChapterListView;
import cz.uhk.zlesak.threejslearningapp.views.layouts.BaseLayout;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListView;
import cz.uhk.zlesak.threejslearningapp.views.quizes.QuizListView;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("administration")
@Tag("administration-view")
@Scope("prototype")
@RolesAllowed({"ADMIN", "TEACHER"})
public class AdministrationView extends BaseLayout {

    private final ChapterService chapterService;
    private final ModelService modelService;
    private final QuizService quizService;

    private TabSheet navigationTabs;

    private Tab chaptersTab;
    private Tab modelsTab;
    private Tab quizzesTab;

    private ChapterListView chapterListView;
    private ModelListView modelListView;
    private QuizListView quizListView;

    @Autowired
    public AdministrationView(ChapterService chapterService, ModelService modelService, QuizService quizService) {
        this.chapterService = chapterService;
        this.modelService = modelService;
        this.quizService = quizService;

        buildLayout();
    }

    private void buildLayout() {
        chaptersTab = new Tab(text("administration.tab.chapters"));
        modelsTab = new Tab(text("administration.tab.models"));
        quizzesTab = new Tab(text("administration.tab.quizzes"));

        chapterListView = new ChapterListView(chapterService);
        modelListView = new ModelListView();
        quizListView = new QuizListView();

        navigationTabs = new TabSheet();
        navigationTabs.add(chaptersTab, chapterListView);
        navigationTabs.add(modelsTab, modelListView);
        navigationTabs.add(quizzesTab, quizListView);
        navigationTabs.setWidthFull();


        Button createButton = new Button(text("button.createChapter"), VaadinIcon.PLUS.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> navigateToCreate());
        navigationTabs.setSuffixComponent(createButton);

        navigationTabs.addSelectedChangeListener(event -> {
            Tab selectedTab = navigationTabs.getSelectedTab();
            if (selectedTab == chaptersTab) {
                createButton.setText(text("button.createChapter"));

            } else if (selectedTab == modelsTab) {
                createButton.setText(text("button.createModel"));
                modelListView.listModels(true);
            } else if (selectedTab == quizzesTab) {

                createButton.setText(text("button.createQuiz"));
            }
        });

        getContent().add(navigationTabs);
        getContent().setSizeFull();
    }

    private void navigateToCreate() {
        Tab selectedTab = navigationTabs.getSelectedTab();
        if (selectedTab == chaptersTab) {
            getUI().ifPresent(ui -> ui.navigate("createChapter"));
        } else if (selectedTab == modelsTab) {
            getUI().ifPresent(ui -> ui.navigate("createModel"));
        } else if (selectedTab == quizzesTab) {
            getUI().ifPresent(ui -> ui.navigate("createQuiz"));
        }
    }

    @Override
    public String getPageTitle() {
        return text("page.title.administrationView");
    }
}

