package uz.uzkassa.horeca.dto.stockadjustment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.06.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockAdjustmentItemDetailDTO implements Serializable {

    Long id;

    CommonDTO product;

    BigDecimal qty;

    BigDecimal price;

    CommonDTO warehouse;

    List<String> aggregationCodes;

    List<String> markCodes;

    List<String> blockCodes;
}
