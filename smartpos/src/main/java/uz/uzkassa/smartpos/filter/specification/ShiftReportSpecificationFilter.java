package uz.uzkassa.smartpos.filter.specification;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftReportSpecificationFilter {
    private Long companyId;
    private Long branchId;
    private LocalDateTime from;
    private LocalDateTime to;
    private Long terminalId;
    private Long userId;
    private String search;
    private String orderBy;
    @Builder.Default
    private SortTypeEnum sortOrder = SortTypeEnum.desc;

    public String getSearchForQuery() {
        if (StringUtils.isNotBlank(search)) {
            return "%" + search.toLowerCase() + "%";
        } else {
            return null;
        }
    }

}
