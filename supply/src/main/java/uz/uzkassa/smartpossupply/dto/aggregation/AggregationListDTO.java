package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationListDTO implements Serializable {

    Long id;

    String code;

    String printCode;

    ProductCommonDTO product;

    AggregationStatus status;

    PackageType packageType;

    Integer qty;

    Integer qtyInUnit;

    LocalDateTime aggregationDate;

    LocalDate productionDate;

    LocalDate expirationDate;

    CommonDTO creator;

    CommonDTO stock;

    CommonDTO productSerial;
}
