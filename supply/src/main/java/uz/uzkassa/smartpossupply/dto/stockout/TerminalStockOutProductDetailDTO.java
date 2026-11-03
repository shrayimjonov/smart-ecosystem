package uz.uzkassa.smartpossupply.dto.stockout;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.discount.DiscountListDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.DiscountValueType;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalStockOutProductDetailDTO {

    Long stockProductId;

    Long id; //productId

    String name; //product name

    String vatBarCode; //IKPU

    String barcode; //Shtrix code

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    boolean hasMark;

    boolean hasMarking;

    ProductType productType;

    BigDecimal qty; //soni

    BigDecimal sellingPrice; // 1 donasini narxi. QQS bilan

    BigDecimal sellingPriceWithoutVat; // 1 donasini narxi QQS siz

    BigDecimal totalSellingPrice; // qty * sellingPrice // n tasini narxi QQS bilan

    BigDecimal totalSellingPriceWithoutVat; // qty * sellingPriceWithoutVat // n tasini narxi QQS siz

    BigDecimal vatRate; //13% 15%

    BigDecimal vatPrice; //1 donasini QQS

    BigDecimal totalVatPrice; //qty * vatPrice // n tasini QQS

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    UnitDTO unit;

    BigDecimal requestedQty;

    BigDecimal catalogPrice;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    ProductSerialLookUpDTO productSerial;

    CompanyCommittentLookUpDTO companyCommittent;

    CommonDTO productContract;

    String packageName;
    String packageCode;

    BigDecimal qtyInWarehouse;
    BigDecimal reservationQty;
    BigDecimal qtyInWarehouseBySerial;
    BigDecimal qtyInWarehouseByContract;
    BigDecimal qtyInWarehouseByCommittent;
    BigDecimal qtyInOrders;

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

    String discountProductUniqueId;

    String parentDiscountProductUniqueId;

    DiscountValueType discountValueType;

    BigDecimal discountLimit;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    @Schema
    List<TerminalStockOutProductDetailDTO> discountProducts = new ArrayList<>();

    List<BlockProductDTO>  blockProducts = new ArrayList<>();
}
