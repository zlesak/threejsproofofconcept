package cz.uhk.zlesak.threejslearningapp.application.components.scrollers;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;

public class ChapterContentScroller extends Scroller {

    public ChapterContentScroller(EditorJsComponent editorjs, MarkdownEditor mdEditor) {
        super(new VerticalLayout(), ScrollDirection.VERTICAL);
        VerticalLayout scrollerVerticalLayout = (VerticalLayout) getContent();
        scrollerVerticalLayout.add(editorjs, mdEditor);
        scrollerVerticalLayout.setMargin(false);
        scrollerVerticalLayout.setPadding(false);
        scrollerVerticalLayout.setSpacing(false);
        scrollerVerticalLayout.setSizeFull();
        setSizeFull();
    }
}
