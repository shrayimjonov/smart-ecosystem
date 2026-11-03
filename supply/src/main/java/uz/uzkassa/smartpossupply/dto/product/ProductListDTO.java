package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductListDTO extends CommonDTO {

    String shortName;

    String barcode;

    String vatBarcode;

    List<CommonDTO> categories;

    CommonDTO branch;

    Vat vat;

    UnitDTO unit;

    BigDecimal price;

    BigDecimal foreignPrice;

    BigDecimal exchangeRate;

    CurrencyType currencyType;

    BigDecimal exciseRate;

    BigDecimal exciseSum;

    String capacity;

    boolean hasMark;

    CommonStatus status;

    BigDecimal brutto;

    BigDecimal netto;

    BigDecimal qtyInPackage;

    List<BlockProductDTO> blockProducts = new ArrayList<>();

    List<ProductPhotoListDTO> photos = new ArrayList<>();

    public void addPhoto(ProductPhotoListDTO productPhotoListDTO) {
        photos.add(productPhotoListDTO);
    }

    public ProductListDTO(String shortName, String barcode, String vatBarcode, List<CommonDTO> categories, CommonDTO branch, Vat vat, UnitDTO unit, BigDecimal price, String capacity, boolean hasMark, CommonStatus status, BigDecimal brutto, BigDecimal netto) {
        this.shortName = shortName;
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.categories = categories;
        this.branch = branch;
        this.vat = vat;
        this.unit = unit;
        this.price = price;
        this.capacity = capacity;
        this.hasMark = hasMark;
        this.status = status;
        this.brutto = brutto;
        this.netto = netto;
    }

    public ProductListDTO(Long id, String name, String shortName, String barcode, String vatBarcode, List<CommonDTO> categories, CommonDTO branch, Vat vat, UnitDTO unit, BigDecimal price, String capacity, boolean hasMark, CommonStatus status, BigDecimal brutto, BigDecimal netto) {
        super(id, name);
        this.shortName = shortName;
        this.barcode = barcode;
        this.vatBarcode = vatBarcode;
        this.categories = categories;
        this.branch = branch;
        this.vat = vat;
        this.unit = unit;
        this.price = price;
        this.capacity = capacity;
        this.hasMark = hasMark;
        this.status = status;
        this.brutto = brutto;
        this.netto = netto;
    }
}
