package uz.uzkassa.smartpossupply.dto.stockout;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutProductDTO extends StockProductBaseDTO {

    Long stockInProductId;

    Long warehouseId;

    BigDecimal priceListPrice;

    BigDecimal requestedQty;

    Long productSerialId;
    Long companyCommittentId;

    Long orderProductId;

    BigDecimal markupPercent;
    BigDecimal markupPrice;

    BigDecimal basePrice;

    BigDecimal exciseRate;
    BigDecimal exciseSum;

    Long productContractId;

    Long lgotaId;

    Long discountId;

    Long discountItemProductId;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    BigDecimal lgotaVatSum;

    String packageName;
    String packageCode;

    BigDecimal scannedQty;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    BigDecimal totalDiscountPrice;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    List<CheckedCodeDTO> aggregationCodes;

    List<CheckedCodeDTO> markCodes;

    List<CheckedCodeDTO> aggregationBlockCodes;
}
