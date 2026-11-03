package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationDetailDTO implements Serializable {

    Long id;

    ProductCommonDTO product;

    Integer qty;

    Integer qtyInUnit;

    String code;

    String printCode;

    String description;

    PackageType packageType;

    Set<String> marks = new HashSet<>();

    String reportId;

    CommonDTO stock;

    LocalDate productionDate;

    LocalDate expirationDate;

    LocalDateTime aggregationDate;

}
