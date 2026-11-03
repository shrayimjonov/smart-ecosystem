package uz.uzkassa.smartpos.dto.filter;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.enums.SortTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanySpecificationFilter {
    private Long regionId;
    private Long cityId;
    private Long managerId;
    private Long companyId;
    private Long activityTypeId;
    private String businessType;
    private Boolean freeTrial;
    private String inn;
    private LocalDateTime from;
    private LocalDateTime to;
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
