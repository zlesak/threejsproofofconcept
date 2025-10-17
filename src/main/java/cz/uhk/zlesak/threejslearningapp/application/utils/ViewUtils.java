package cz.uhk.zlesak.threejslearningapp.application.utils;

import com.vaadin.flow.router.Location;

public class ViewUtils {
    public static int[] extractPageAndLimit(Location location) {
        int page = 1;
        int limit = 10;
        var params = location.getQueryParameters().getParameters();
        if (params.containsKey("page")) {
            page = Integer.parseInt(params.get("page").getFirst());
        }
        if (params.containsKey("limit")) {
            limit = Integer.parseInt(params.get("limit").getFirst());
        }
        return new int[]{page, limit};
    }
}
