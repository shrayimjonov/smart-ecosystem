package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StatisticsByEmployeeReportDTO implements Serializable {

    CommonDTO user;

    CommonDTO branch;

    Long customerTotal;

    Long customerBranchTotal;

    Long deviceTotal;

    Long fiscalModuleTotal;

    Long tariffTotal;

    Long tariffTotalBonus;

    Long leasingTotal;

    Long leasingTotalBonus;

    Long tradeInTotal;

    Long tradeInTotalBonus;

    Long serviceTotal;

    Long serviceTotalBonus;

    Long rentTotal;

    Long rentTotalBonus;
}
