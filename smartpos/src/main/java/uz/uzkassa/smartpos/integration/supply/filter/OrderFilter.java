package uz.uzkassa.smartpos.integration.supply.filter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderFilter {
    private Long customerId;
    private Long deliveryBranchId;
    private Integer page = 0;
    private Integer size = 20;
    private LocalDateTime from;
    private LocalDateTime to;
    private String orderBy;
    private SortTypeEnum sortType;
    private String search;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase().replace("_", "\\_") + "%" : search;
    }
}
