package uz.uzkassa.smartpossupply.dto.stocktransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.product.BlockProductDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpeditorStockTransferProductDetailDTO implements Serializable {

    Long stockTransferProductId;

    Long id; //productId

    String name; //product name

    String vatBarCode; //IKPU

    String barcode; //Shtrix code

    String packageName;
    String packageCode;

    Vat vat;

    CompanyCommittentLookUpDTO companyCommittent;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    boolean hasMark;

    boolean hasMarking;

    ProductType productType;

    BigDecimal qty; //soni

    BigDecimal soldQty;

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

    BigDecimal catalogPrice;

    BigDecimal totalPrice;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    Integer totalQtyInPackage;

    List<BlockProductDTO> blockProducts;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonDTO parentWarehouse;

    List<PackageQuantityDTO> packageQuantities;

    BigDecimal blockQty;

    BigDecimal soldQtyInBlock;
}
