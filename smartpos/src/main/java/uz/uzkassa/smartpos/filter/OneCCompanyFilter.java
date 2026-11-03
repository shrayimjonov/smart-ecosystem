package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

@Getter
@Setter
public class OneCCompanyFilter {
    private Long receiptCountFrom;
    private Long receiptCountTo;
    private String search;
    private Integer page = 0;
    private Integer size = 200;
    private String orderBy;
    private SortTypeEnum sortOrder;

    public String getSearchForQuery() {
        return StringUtils.isNotBlank(search) ? "%" + search.toLowerCase() + "%" : null;
    }

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }
}
