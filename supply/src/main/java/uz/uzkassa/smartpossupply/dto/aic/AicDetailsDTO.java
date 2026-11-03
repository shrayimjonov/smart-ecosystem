package uz.uzkassa.smartpossupply.dto.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.AicResponse;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonAicDTO;
import uz.uzkassa.smartpossupply.enums.AicAdditionalUnit;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicDetailsDTO {

    Long id;

    String aic;

    ProductCommonAicDTO product;

    ProductType productType;

    int qty;

    BigDecimal markQty;

    BigDecimal tnvedAddUnits;

    BigDecimal tnvedAddUnitsCount;

    AicAdditionalUnit additionalUnit;

    Set<String> aggregations = new LinkedHashSet<>();

    Set<String> blocks = new LinkedHashSet<>();

    Set<String> marks = new LinkedHashSet<>();

    LocalDateTime createdAt;

    AicStatus status;

    CommonDTO creator;

    String reportContent;

    AicResponse forSign;
}
