package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationHistoryListDTO implements Serializable {

    Long id;

    String code;

    ProductCommonDTO product;

    Long entityId;

    EntityTypeEnum entityType;

    String entityNumber;

    String transferType;

    String codeType;

    LocalDateTime createDate;

    String creator;
}
