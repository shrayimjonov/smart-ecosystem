package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.unit.UnitDTO;
import uz.uzkassa.smartpossupply.enums.MarketplaceApplicationStatus;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationDetailDTO implements Serializable {

    Long id;

    String nameUz;

    String nameCyrillic;

    String nameRu;

    String descriptionUz;

    String descriptionCyrillic;

    String descriptionRu;

    String barcode;

    String catalogCode;

    String catalogName;

    String packageCode;

    String packageName;

    Vat vat;

    UnitDTO unit;

    BigDecimal price;

    BigDecimal catalogPrice;

    CommonDTO country;

    String rejectedReason;

    MarketplaceApplicationStatus status;
}
