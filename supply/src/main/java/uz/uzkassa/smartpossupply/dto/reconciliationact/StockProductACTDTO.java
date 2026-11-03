package uz.uzkassa.smartpossupply.dto.reconciliationact;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductACTDTO {
    Long id;
    String stockNumber;
    Long stockOutId;
    CommonDTO product;
    CommonDTO customerBranch;
    BigDecimal qty;
    BigDecimal sellingPrice;
    BigDecimal totalSellingPrice;
}
