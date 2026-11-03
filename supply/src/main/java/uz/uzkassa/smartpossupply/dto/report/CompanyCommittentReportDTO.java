package uz.uzkassa.smartpossupply.dto.report;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2024
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCommittentReportDTO extends BalanceReportDTO {

    CompanyBaseDTO committent;

    //List<CommonDTO> productSerials;
}
