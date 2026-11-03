package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AccountReceivableReportDTO {

    CompanyBaseDTO customer;

    CommonDTO financialOperation;

    FinancialOperationType operationType;

    CommonDTO stockOut;

    FacturaLinkDTO factura;

    CommonDTO agent;

    CommonDTO expeditor;

    LocalDateTime operationDate;

    CommonDTO planRoute;

    BigDecimal totalDebit;

    BigDecimal totalCredit;

    Long overdueDays;
}
