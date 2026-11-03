package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptBranchDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ZReportDTO extends ShiftReportBaseTotalDTO {

    Long id;

    CommonDTO shift;

    Long fiscalNumber;

    CompanyBaseDTO company;

    ReceiptBranchDTO branch;

    CommonDTO cashier;

    LocalDateTime startDate;

    LocalDateTime endDate;

    ZReportStatisticsDTO statistics;
}
