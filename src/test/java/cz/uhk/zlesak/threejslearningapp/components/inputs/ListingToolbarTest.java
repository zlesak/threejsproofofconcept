package cz.uhk.zlesak.threejslearningapp.components.inputs;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.select.Select;
import cz.uhk.zlesak.threejslearningapp.events.threejs.SearchEvent;
import cz.uhk.zlesak.threejslearningapp.testsupport.VaadinTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListingToolbarTest {

    @BeforeEach
    void setUp() {
        VaadinTestSupport.setCurrentUi();
    }

    @AfterEach
    void tearDown() {
        VaadinTestSupport.clearCurrentUi();
    }

    @Test
    void everyControlHasAVisibleLabel() {
        // The selects described themselves with setHelperText, which renders below the field and is not
        // the field's accessible name, and the search field had only a placeholder — which vanishes as
        // soon as the user types.
        ListingToolbar toolbar = new ListingToolbar();

        assertEquals("Hledat výraz", toolbar.getSearchField().getLabel());
        assertEquals("Řadit podle", orderBySelect(toolbar).getLabel());
        assertEquals("Směr řazení", directionSelect(toolbar).getLabel());

        assertNotNull(orderBySelect(toolbar).getLabel());
        assertTrue(orderBySelect(toolbar).getHelperText() == null || orderBySelect(toolbar).getHelperText().isBlank());
        assertTrue(directionSelect(toolbar).getHelperText() == null || directionSelect(toolbar).getHelperText().isBlank());
    }

    @Test
    void thePlaceholderSurvivesAsAnExampleNotAsTheLabel() {
        ListingToolbar toolbar = new ListingToolbar();

        assertEquals("Hledat...", toolbar.getSearchField().getPlaceholder());
        assertFalse(toolbar.getSearchField().getLabel().isBlank());
    }

    @Test
    void searchFieldChangesShouldToggleControlsAndFireResetSearchEvent() {
        ListingToolbar toolbar = new ListingToolbar();
        UI.getCurrent().add(toolbar);
        AtomicReference<SearchEvent> fired = new AtomicReference<>();
        ComponentUtil.addListener(toolbar, SearchEvent.class, fired::set);

        toolbar.getSearchField().setValue("atlas");

        assertFalse(orderBySelect(toolbar).isEnabled());
        assertFalse(directionSelect(toolbar).isEnabled());
        assertTrue(searchButton(toolbar).isEnabled());

        toolbar.getSearchField().clear();

        assertTrue(orderBySelect(toolbar).isEnabled());
        assertTrue(directionSelect(toolbar).isEnabled());
        assertFalse(searchButton(toolbar).isEnabled());
        assertEquals("", fired.get().getValue());
    }

    @Test
    void searchButtonAndSelectsShouldEmitSearchEvent() {
        ListingToolbar toolbar = new ListingToolbar();
        UI.getCurrent().add(toolbar);
        AtomicReference<SearchEvent> fired = new AtomicReference<>();
        ComponentUtil.addListener(toolbar, SearchEvent.class, fired::set);

        toolbar.getSearchField().setValue("bone");
        directionSelect(toolbar).setValue(Sort.Direction.DESC);
        orderBySelect(toolbar).setValue("Created");
        searchButton(toolbar).click();

        assertEquals("bone", fired.get().getValue());
        assertEquals(Sort.Direction.DESC, fired.get().getSortDirection());
        assertEquals("Created", fired.get().getOrderBy());
    }

    @Test
    void setSearchFieldValueAndFieldExtractionShouldUseAllowedNonIdFields() throws Exception {
        ListingToolbar toolbar = new ListingToolbar();

        toolbar.setSearchFieldValue("lebka");

        assertEquals("lebka", toolbar.getSearchField().getValue());

        Method method = ListingToolbar.class.getDeclaredMethod("extractFieldNames", Class.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> fields = (List<String>) method.invoke(toolbar, cz.uhk.zlesak.threejslearningapp.domain.common.AbstractEntity.class);

        assertTrue(fields.contains("name"));
        assertTrue(fields.contains("created"));
        assertTrue(fields.contains("updated"));
        assertTrue(fields.contains("description"));
        assertFalse(fields.contains("id"));
        assertFalse(fields.contains("creatorId"));
    }

    @SuppressWarnings("unchecked")
    private Select<String> orderBySelect(ListingToolbar toolbar) {
        try {
            var field = ListingToolbar.class.getDeclaredField("orderBySelect");
            field.setAccessible(true);
            return (Select<String>) field.get(toolbar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Select<Sort.Direction> directionSelect(ListingToolbar toolbar) {
        try {
            var field = ListingToolbar.class.getDeclaredField("searchDirectionSelect");
            field.setAccessible(true);
            return (Select<Sort.Direction>) field.get(toolbar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Button searchButton(ListingToolbar toolbar) {
        try {
            var field = ListingToolbar.class.getDeclaredField("createButton");
            field.setAccessible(true);
            return (Button) field.get(toolbar);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
