package uz.uzkassa.smartpossupply.dto.stockreturn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.03.2023 16:59
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockReturnListDTO {

    Long id;

    String stockNumber;

    StockStatus status;

    LocalDateTime transferDate;

    CompanyBaseDTO customer;

    CommonDTO branch;

    CommonDTO warehouse;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;

    CurrencyType currencyType;

    FacturaLinkDTO factura;

    LocalDateTime lastModifiedDate;
}
