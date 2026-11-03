package uz.uzkassa.smartpossupply.dto.productdetail;


import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.Month;
import uz.uzkassa.smartpossupply.enums.TargetAudience;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailDTO {

    Integer width;

    Integer height;

    Integer length;

    Integer packageWidth;

    Integer packageHeight;

    Integer packageLength;

    String manufacturer;

    String articleNumber;

    String description;

    String material;

    String colour;

    String model;

    LocalDateTime expireDate;

    LocalDateTime productionDate;

    String composition;

    String recipe;

    String purpose;

    Integer ageFrom;

    Integer ageTo;

    String targetAudience;

    Set<String> seoKeywords;

    Set<Month> seasonality;

    Long productionCountryId;

    String productionCountryName;
}
