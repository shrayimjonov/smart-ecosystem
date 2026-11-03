package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalProductListDTO extends CommonDTO {

    String barcode;

    String vatBarcode;

    Long unitId;

    String unitName;

    Vat vat;

    BigDecimal price;

    String packageCode;

    String packageName;

    List<BlockProductDTO> blockProducts;
}
