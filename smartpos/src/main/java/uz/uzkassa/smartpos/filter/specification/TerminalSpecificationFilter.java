package uz.uzkassa.smartpos.filter.specification;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.dto.filter.management.TerminalFilter;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminalSpecificationFilter {
    private Long branchId;
    private Long companyId;
    private LocalDateTime from;
    private LocalDateTime billFrom;
    private LocalDateTime to;
    private LocalDateTime billTo;
    private String search;
    private String orderBy;
    @Builder.Default
    private SortTypeEnum sortOrder = SortTypeEnum.desc;

    public String getSearchForQuery() {
        return StringUtils.isNotEmpty(search) ? "%" + search.toLowerCase().replace("_", "\\_") + "%" : search;
    }

    public TerminalSpecificationFilter(TerminalFilter filter) {
        this.branchId = filter.getBranchId();
        this.companyId = filter.getCompanyId();
        this.from = filter.getFrom();
        this.billFrom = filter.getBillFrom();
        this.to = filter.getTo();
        this.billTo = filter.getBillTo();
        this.search = filter.getSearch();
        this.orderBy = filter.getOrderBy();
        this.sortOrder = filter.getSortOrder();
    }
}
