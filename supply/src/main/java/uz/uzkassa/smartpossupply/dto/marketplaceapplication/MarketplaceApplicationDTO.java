package uz.uzkassa.smartpossupply.dto.marketplaceapplication;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarketplaceApplicationDTO  implements Serializable {

    Long id;

    String nameUz;

    String nameCyrillic;

    String nameRu;

    String descriptionUz;

    String descriptionCyrillic;

    String descriptionRu;

    @NotNull
    BigDecimal price;

    Long countryId;

    List<Long> deletedPhotoIds;
}
