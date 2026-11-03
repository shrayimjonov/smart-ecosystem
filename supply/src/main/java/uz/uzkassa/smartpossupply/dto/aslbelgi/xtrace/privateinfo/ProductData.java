package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace.privateinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductData implements Serializable {

    String productId;

    Integer productGroupId;

    String categoryId;

    LocalDateTime productionDate;

    LocalDateTime expirationDate;

    String productSeries;

    String manufacturerCountry;

    Boolean mixedProductGroups;

    Boolean mixedCategories;
}
