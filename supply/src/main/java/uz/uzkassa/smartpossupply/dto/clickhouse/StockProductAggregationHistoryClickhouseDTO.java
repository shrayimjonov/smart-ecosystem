package uz.uzkassa.smartpossupply.dto.clickhouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.05.2023 16:46
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductAggregationHistoryClickhouseDTO extends AuditClickhouseDTO {

    Long id;

    @NotNull
    Long companyId;
    String companyName;
    String companyTin;

    String code;
    String codeType;
    String transferType;

    Long entityId;
    String entityType;
    String entityNumber;

    Long productId;
    String productName;
    String productBarcode;
    String productVatBarcode;
}
