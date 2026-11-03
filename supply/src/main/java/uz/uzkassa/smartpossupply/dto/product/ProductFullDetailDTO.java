package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryEnvironmentDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.productdetail.ProductDetailDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.DispenseType;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.enums.ProductType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFullDetailDTO extends CommonDTO {

    String shortName;

    String barcode;

    List<CategoryEnvironmentDTO> categories;

    CommonDTO branch;

    ProductType productType;

    Vat vat;

    UnitDTO unit;

    BigDecimal purchasePrice;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    CompanyCommittentLookUpDTO companyCommittent;

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

    String tnved;

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    String aic;

    ProductOrigin origin;

    @Deprecated
    ProductCommonDTO parent;

    String boxBarcode;

    String blockBarcode;

    CommonStatus status;

    BigDecimal weightBrutto;

    BigDecimal weightNetto;

    List<BlockProductDTO> blockProducts = new ArrayList<>();

    Integer expiredInMonth;

    String brand;

    ProductDetailDTO detail;

    DispenseType dispenseType;

    public void addPhoto(ProductPhotoListDTO photo) {
        photos.add(photo);
    }
}
