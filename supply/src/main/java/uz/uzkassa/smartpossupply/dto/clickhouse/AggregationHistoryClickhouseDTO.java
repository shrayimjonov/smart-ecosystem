package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationHistoryClickhouseDTO extends AuditClickhouseDTO {

    Long id;

    String code;

    String codeType;

    String transferType;

    Long entityId;
    String entityType;
    String entityNumber;

    Long productId;
    String productName;
    String productBarcode;

    String action;
}
