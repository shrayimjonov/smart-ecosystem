package uz.uzkassa.smartpos.dto.catalog;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.ValidationField;
import uz.uzkassa.smartpos.dto.base.CommonIdDto;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductValidationDto implements Serializable {
    private ValidationField<String> name;
    private ValidationField<String> barcode;
    private ValidationField<String> vatBarcode;
    private ValidationField<Long> category;
    private ValidationField<Long> unit;
    private ValidationField<BigDecimal> salesPrice;
    private ValidationField<BigDecimal> vat;
    private ValidationField<Boolean> favorite;
    private ValidationField<Boolean> hasMark;

    public boolean isValid() {
        return name.isValid() &&
                barcode.isValid() &&
                vatBarcode.isValid() &&
                category.isValid() &&
                unit.isValid() &&
                salesPrice.isValid() &&
                vat.isValid() &&
                favorite.isValid() &&
                hasMark.isValid();
    }

    public Product1CCreateDto toCreateDto(Long branchId) {
        return new Product1CCreateDto(null,
                name.getValue(),
                barcode.getValue(),
                vatBarcode.getValue(),
                null,
                vat.getValue(),
                hasMark.getValue(),
                favorite.getValue(),
                new CommonIdDto(unit.getValue()),
                new CommonIdDto(category.getValue()),
                salesPrice.getValue(),
                branchId,
                null,
                null,
                (short) 2);
    }

}
