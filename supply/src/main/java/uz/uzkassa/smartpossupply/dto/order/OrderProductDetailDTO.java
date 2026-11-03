package uz.uzkassa.smartpossupply.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductDetailDTO extends OrderProductBaseDTO {

    Long orderProductId;

    Long id;

    String name;

    String barcode;

    String vatBarcode;

    UnitDTO unit;

    BigDecimal qtyInWarehouse;
    BigDecimal qtyInWarehouseBySerial;
    BigDecimal qtyInWarehouseByContract;
    BigDecimal qtyInWarehouseByCommittent;
    BigDecimal reservationQty;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    boolean hasMark;

    ProductType productType;

    BigDecimal catalogPrice;
    BigDecimal catalogForeignPrice;

    BigDecimal markupPercent;
    BigDecimal markupPrice;

    CompanyCommittentLookUpDTO companyCommittent;

    LgotaLookUpDTO lgota;
    BigDecimal lgotaVatSum;

    LocalDate expiryDate;
    String code1C;
    BigDecimal basePrice;

    BigDecimal brutto;

    BigDecimal netto;

    ProductSerialLookUpDTO productSerial;

    CommonDTO productContract;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    CommonDTO warehouse;

    String packageCode;

    String packageName;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal totalDiscountPrice;

    BigDecimal discountPercent;

    BigDecimal discountPrice;

    DiscountListDTO discount;

    CommonDTO discountItemProduct;

    Integer totalQtyInPackage;

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    DiscountValueType discountValueType;

    BigDecimal discountLimit;

    List<BlockProductDTO> blockProducts = new ArrayList<>();

    @Schema
    List<OrderProductDetailDTO> discountProducts = new ArrayList<>();

    List<PackageQuantityDTO> packageQuantities;
}
