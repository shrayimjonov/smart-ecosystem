package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockTransferProductDetailDTO {

    Long stockTransferProductId;

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

    UnitDTO unit;

    CommonDTO order;

    Integer aggregationQty;

    Integer markQty;

    Integer aggregationBlockQty;

    ProductSerialLookUpDTO productSerial;

    BigDecimal qtyInWarehouse;

    BigDecimal reservationQty;

    BigDecimal qtyInWarehouseBySerial;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    BigDecimal scannedQty;

    BigDecimal totalBrutto;

    BigDecimal totalNetto;

    BigDecimal brutto;

    BigDecimal netto;

    BigDecimal price;

    BigDecimal totalPrice;

    Integer totalQtyInPackage;

    List<BlockProductDTO> blockProducts;
}
