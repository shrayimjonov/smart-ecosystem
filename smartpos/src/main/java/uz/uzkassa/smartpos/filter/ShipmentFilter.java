package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.smartpos.enums.ShipmentStatus;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentFilter {
    private Long companyId;
    private LocalDateTime from;
    private LocalDateTime to;
    private ShipmentStatus status;
    private String search;
    private Integer page = 0;
    private Integer size = 20;
    private String orderBy;
    private SortTypeEnum sortOrder = SortTypeEnum.desc;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    public String getSearchForQuery() {
        return StringUtils.isNotBlank(search) ? "%" + search.toLowerCase() + "%" : null;
    }
}
