package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductLookUpDTO extends CommonDTO {

    Vat vat;

    UnitDTO unit;

    BigDecimal purchasePrice;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    String barcode;

    String vatBarcode;

    String catalogName;

    String packageName;

    String packageCode;

    String capacity;

    boolean hasMark;

    BigDecimal qtyInWarehouse;

    ProductType productType;

    CompanyCommittentLookUpDTO companyCommittent;

    ProductOrigin origin;

    String boxBarcode;

    String blockBarcode;

    @Deprecated
    ProductCommonDTO parent;

    BigDecimal weightBrutto;

    BigDecimal weightNetto;

    BigDecimal qtyInPackage;

    BigDecimal qtyInBox;

    BigDecimal qtyInPallet;

    List<BlockProductDTO> blockProducts;

    DispenseType dispenseType;
}
