package uz.uzkassa.smartpossupply.dto.sync.marketplace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationSyncDTO implements Serializable {

    Long companyId;

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

    Long measureId;

    BigDecimal price;

    String countryCode;

    List<MarketplaceApplicationPhotoSyncDTO> photos;
}
