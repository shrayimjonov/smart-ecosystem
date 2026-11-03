package uz.uzkassa.smartpossupply.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.productdetail.ProductDetailDTO;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.DispenseType;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO implements Serializable {

    String tasnifId;

    Long id;

    @NotBlank
    String name;

    String shortName;

    String brand;

    String barcode;

    String aggregationCode;

    String markCode;

    List<Long> categoryIds;

    @NotNull
    Long branchId;

    ProductType productType;

    @NotNull
    Vat vat;

    Long unitId;

    BigDecimal purchasePrice;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    Long companyCommittentId;

    boolean favourite;

    boolean hasMark;

    String vatBarcode;
    String catalogName;

    String packageName;
    String packageCode;

    BigDecimal qtyInPackage;
    BigDecimal qtyInBox;
    BigDecimal qtyInPallet;

    String capacity;

    Long companyId;

    String tnved;

    ProductOrigin origin;

    Long parentId;

    String boxBarcode;

    String blockBarcode;

    BigDecimal weightBrutto;

    BigDecimal weightNetto;

    String additionalUnit;

    ProductPhotoDTO productPhoto;

    List<BlockProductDTO> blockProducts;

    Integer expiredInMonth;

    ProductDetailDTO detail;

    DispenseType dispenseType;
}
