package cz.uhk.zlesak.threejslearningapp.common;

import com.vaadin.flow.router.Location;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;

/**
 * Utility class for extracting filter request parameters from a Location object.
 */
public class ViewUtils {
    /**
     * Extracts filter request parameters from the given Location object.
     * @param location the Location object containing query parameters
     * @return an array of extracted parameters: page, limit, orderBy, sortDirection, searchText
     */
    public static Object[] extractFilterRequestParameters(Location location) {
        int page = 1;
        int limit = 10;
        String orderBy = "Name";
        SortDirectionEnum sortDirection = SortDirectionEnum.ASC;
        String searchText = "";

        var params = location.getQueryParameters().getParameters();
        if (params.containsKey("page")) {
            page = Integer.parseInt(params.get("page").getFirst());
        }
        if (params.containsKey("limit")) {
            limit = Integer.parseInt(params.get("limit").getFirst());
        }
        if (params.containsKey("orderBy")) {
            orderBy = String.valueOf(params.get("orderBy").getFirst());
        }
        if (params.containsKey("sortDirection")) {
            sortDirection = SortDirectionEnum.valueOf(params.get("sortDirection").getFirst());
        }
        if (params.containsKey("searchText")) {
            searchText = String.valueOf(params.get("searchText").getFirst());
        }
        return new Object[]{page, limit, orderBy, sortDirection, searchText};
    }
}
