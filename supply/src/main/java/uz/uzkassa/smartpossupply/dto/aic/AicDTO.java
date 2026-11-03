package uz.uzkassa.smartpossupply.dto.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicDTO {

    Long productId;

    String tnVed;

    int qty;

    BigDecimal tnvedAddUnits;

    BigDecimal tnvedAddUnitsCount;

    List<String> aggregationCodes;

    List<String> markCodes;

    List<String> aggregationBlockCodes;

    BigDecimal markQty;
}
