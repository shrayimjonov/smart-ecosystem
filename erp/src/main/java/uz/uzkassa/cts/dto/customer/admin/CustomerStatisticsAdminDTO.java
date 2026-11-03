package uz.uzkassa.cts.dto.customer.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerStatisticsAdminDTO extends BaseStatisticsDTO {

    Long customerBranchCount;
}
