package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ShiftReportFilter {
    private LocalDateTime from;
    private LocalDateTime to;
    private Long userId;
    private Set<Long> branchIds;
    private Set<Long> branchGroupIds;
    private String search;
    private int page = 0;
    private int size = 20;

    public Pageable getPageable() {
        return Pageable.ofSize(size).withPage(page);
    }

    public String getSearchForQuery() {
        return search == null ? null : "%" + search.toLowerCase() + "%";
    }
}
