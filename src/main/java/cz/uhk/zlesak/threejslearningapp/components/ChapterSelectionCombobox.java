package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.combobox.ComboBox;
import cz.uhk.zlesak.threejslearningapp.data.SubChapterForComboBox;

public class ChapterSelectionCombobox extends ComboBox<SubChapterForComboBox> {
//    public ChapterSelectionCombobox() { TODO
//
//        addValueChangeListener(event -> {
//            SubChapterForComboBox oldSelectedSubchapter = event.getOldValue();
//            SubChapterForComboBox newSelectedSubchapter = event.getValue();
//            if (oldSelectedSubchapter != null && newSelectedSubchapter != null) {
//                navigationContentLayout.hideSubchapterNavigationContent(oldSelectedSubchapter.id());
//            }
//            if (newSelectedSubchapter != null) {
//                navigationContentLayout.showSubchapterNavigationContent(newSelectedSubchapter.id());
//                try {
//                    editorjs.setChapterContentData(chapterController.getSelectedSubChapterContent(newSelectedSubchapter.id()));
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
}
