package cz.uhk.zlesak.threejslearningapp.domain.common;

import cz.uhk.zlesak.threejslearningapp.components.common.Filter;
import cz.uhk.zlesak.threejslearningapp.events.threejs.SearchEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class FilterParameters<R> {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "Name");
    R filter;

    public void setFilteredParameters(SearchEvent searchEvent, R filter) {
        this.pageRequest = PageRequest.of(
                0,
                10,
                searchEvent.getSortDirection(),
                searchEvent.getOrderBy()
        );
        this.filter = filter;
    }

    public void setPageNumber(int pageNumber) {
        this.pageRequest = PageRequest.of(
                pageNumber,
                this.pageRequest.getPageSize(),
                this.pageRequest.getSort().iterator().next().getDirection(),
                this.pageRequest.getSort().iterator().next().getProperty()
        );
    }
}
