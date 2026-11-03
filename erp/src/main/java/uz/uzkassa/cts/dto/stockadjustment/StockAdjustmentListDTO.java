package uz.uzkassa.cts.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.enums.StockStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 98028738583L;

    Long id;

    String adjustmentNumber;

    LocalDateTime adjustmentDate;

    CompanyCommonDTO company;

    CommonDTO branch;

    CommonDTO warehouse;

    StockStatus status;

    BigDecimal totalQty;
}
