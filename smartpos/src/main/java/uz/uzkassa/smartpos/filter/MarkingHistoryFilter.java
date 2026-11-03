package uz.uzkassa.smartpos.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;
import uz.uzkassa.smartpos.enums.MarkingStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.io.Serializable;

/**
 * Filter for {@link MarkingHistory}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkingHistoryFilter implements Serializable {
    private Long companyId;
    private Long branchId;
    private MarkingStatus status;
    private String search;
    private Integer page = 0;
    private Integer size = 20;
    private String orderBy;
    private SortTypeEnum sortOrder;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    public String getSearchForQuery() {
        return StringUtils.isNotBlank(search) ? "%" + search + "%" : null;
    }
}
