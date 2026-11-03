package uz.uzkassa.cts.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StatisticsByDistrictReportDTO implements Serializable {

    CommonCodeDTO district;

    Long customerTotal;

    Long customerBranchTotal;

    Long deviceTotal;

    Long fiscalModuleTotal;

    Long fiscalizationTotal;

    Long applicationTotal;
}
