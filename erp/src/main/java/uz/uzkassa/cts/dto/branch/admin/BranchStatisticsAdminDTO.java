package uz.uzkassa.cts.dto.branch.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.customer.admin.BaseStatisticsDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchStatisticsAdminDTO extends BaseStatisticsDTO {

    Long customerCount;

    Long customerBranchCount;

    Long fiscalizationCount;

    Long employeeCount;
}
