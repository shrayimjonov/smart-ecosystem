package uz.uzkassa.smartpos.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.uzkassa.smartpos.enums.SortTypeEnum;


@Setter
@Getter
public class PrimaryFilter {
    private Integer page = 0;
    private Integer size = 20;
    private String search;
    private String orderBy;
    private SortTypeEnum sortOrder;

    @Schema(hidden = true)
    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

    @Schema(hidden = true)
    public Pageable getOrderedPageable() {
        return PageRequest.of(page, size, SortTypeEnum.asc.equals(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);
    }

    @Schema(hidden = true)
    public String getSearchForQuery() {
        return StringUtils.isNotBlank(search) ? "%" + search.toLowerCase() + "%" : null;
    }

    @Schema(hidden = true)
    public Integer getOffset() {
        return page * size;
    }

    @Schema(hidden = true)
    public String getSearchForLookup() {
        return StringUtils.isNotBlank(search) ? search + "%" : null;
    }

}
