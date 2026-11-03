package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.05.2023 16:46
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockProductMarkClickhouseDTO extends AuditClickhouseDTO {

    Long id;

    Long stockProductId;
    String code;
    String printCode;
    String parentCode;

    String transferType;
    String status;
    BigDecimal qty;

    Long productId;
    String productName;
    String productBarcode;
    String productVatBarcode;
}
