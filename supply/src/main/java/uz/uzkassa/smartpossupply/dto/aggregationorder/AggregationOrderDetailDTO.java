package uz.uzkassa.smartpossupply.dto.aggregationorder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationOrderDetailDTO implements Serializable {

    Long id;

    String orderNumber;

    PackageType packageType;

    Integer qty;

    Integer used;

    String description;

    LocalDateTime createdDate;
}
