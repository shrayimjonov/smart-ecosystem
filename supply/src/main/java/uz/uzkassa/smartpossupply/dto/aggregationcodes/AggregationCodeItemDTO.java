package uz.uzkassa.smartpossupply.dto.aggregationcodes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCodeItemDTO implements Serializable {

    Long id;

    String code;

    BigDecimal qty;

    BigDecimal qtyInPackage;

    PackageType packageType;

    LocalDateTime stockDate;

    String status;

    String ownerName;

    String ownerTin;

    String comment;
}
