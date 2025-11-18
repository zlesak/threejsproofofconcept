package cz.uhk.zlesak.threejslearningapp.components.dialogs;

import com.vaadin.flow.component.dialog.Dialog;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.views.chapter.ChapterListView;
import lombok.Setter;

import java.util.function.Consumer;

@Setter
public class ChapterListDialog extends Dialog {
    private Consumer<ChapterEntity> chapterSelectedListener;
    private final ChapterListView chapterListView;

    public ChapterListDialog(ChapterListView chapterListView) {
        this.chapterListView = chapterListView;
        setWidth("800px");
        setHeight("600px");
        add(chapterListView);
    }

    private void onChapterSelected(ChapterEntity entity) {
        if (chapterSelectedListener != null) {
            chapterSelectedListener.accept(entity);
        }
        close();
    }

    @Override
    public void open() {
        this.setOpened(true);
        chapterListView.setChapterSelectedListener(this::onChapterSelected);
        chapterListView.listChapters();
    }
}
