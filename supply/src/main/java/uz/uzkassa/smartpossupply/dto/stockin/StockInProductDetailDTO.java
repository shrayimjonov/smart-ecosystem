package uz.uzkassa.smartpossupply.dto.stockin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockInProductDetailDTO extends StockProductBaseDTO {

    String stockProductName;

    UnitDTO unit;

    boolean hasError;

    BigDecimal qtyInWarehouse;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    CommonDTO productSerial;

    String serialNumber;

    BigDecimal basePrice;

    BigDecimal facturaProfitRate;

    CompanyCommittentLookUpDTO companyCommittent;

    CommonDTO productContract;

    LgotaLookUpDTO lgota;

    BigDecimal lgotaVatSum;

    Set<String> markCodes;

    Set<String> aggregationCodes;

    Set<String> aggregationBlocks;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    String packageCode;
    String packageName;

    BigDecimal turonQty;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    boolean hasWarning;
}
