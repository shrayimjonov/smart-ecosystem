package uz.uzkassa.smartpossupply.dto.stockin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.StockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInListDTO {

    Long id;

    String stockNumber;

    LocalDateTime transferDate;

    CompanyBaseDTO supplier;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonDTO oldStock;

    CommonDTO stockTransfer;

    BigDecimal totalPrice;

    BigDecimal totalForeignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    StockStatus status;

    CommonCodeDTO relation;

    boolean oneCSync;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    LocalDateTime lastModifiedDate;
}
