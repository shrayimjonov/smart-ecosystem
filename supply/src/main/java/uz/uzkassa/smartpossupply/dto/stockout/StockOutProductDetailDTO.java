package uz.uzkassa.smartpossupply.dto.stockout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductBaseDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockOutProductDetailDTO extends StockProductBaseDTO {

    UnitDTO unit;

    BigDecimal requestedQty;

    BigDecimal catalogPrice;

    BigDecimal catalogForeignPrice;

    BigDecimal basePrice;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    BigDecimal priceListPrice;

    ProductSerialLookUpDTO productSerial;

    BigDecimal facturaProfitRate;

    CompanyCommittentLookUpDTO companyCommittent;

    CommonDTO productContract;

    LgotaLookUpDTO lgota;
    BigDecimal lgotaVatSum;

    String packageName;
    String packageCode;

    BigDecimal qtyInWarehouse;
    BigDecimal qtyInOrders;
    BigDecimal reservationQty;
    BigDecimal qtyInWarehouseBySerial;
    BigDecimal qtyInWarehouseByContract;
    BigDecimal qtyInWarehouseByCommittent;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    CommonDTO warehouse;

    BigDecimal scannedQty;

    BigDecimal totalDiscountPrice;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    DiscountListDTO discount;

    CommonDTO discountItemProduct;

    DiscountValueType discountValueType;

    BigDecimal discountLimit;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    @Schema
    List<StockOutProductDetailDTO> discountProducts = new ArrayList<>();
}
