package cz.uhk.zlesak.threejslearningapp.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class FilterParameters {
    int pageNumber = 1;
    int pageSize = 10;
    String orderBy = "Name";
    SortDirectionEnum sortDirection = SortDirectionEnum.ASC;
    String searchText = "";

    public String getLocationQueryParams(String pageName) {
        if(searchText != null && !searchText.isEmpty()) {
            return String.format("%s?searchedText=%s",
                    pageName,
                    searchText);
        }
        return String.format("%s?page=%d&limit=%d&orderBy=%s&sortDirection=%s",
                pageName,
                pageNumber,
                pageSize,
                orderBy,
                sortDirection.name());
    }
}
