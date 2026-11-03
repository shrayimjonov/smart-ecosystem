package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MobileStockOutProductDTO {

    BigDecimal scannedQty;

    List<CheckedCodeDTO> aggregationCodes;

    List<CheckedCodeDTO> markCodes;

    List<CheckedCodeDTO> aggregationBlockCodes;
}
