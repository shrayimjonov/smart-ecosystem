package uz.uzkassa.smartpossupply.dto.shift;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptBranchDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.12.2022 16:32
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XReportDetailDTO implements Serializable {

    CommonDTO shift;

    Long fiscalNumber;

    CompanyBaseDTO company;

    ReceiptBranchDTO branch;

    XReportDTO report;

    CommonDTO cashier;

    LocalDateTime startDate;

    LocalDateTime endDate;

    ZReportStatisticsDTO statistics;
}
