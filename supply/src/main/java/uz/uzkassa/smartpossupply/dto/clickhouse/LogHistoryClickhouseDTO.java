package uz.uzkassa.smartpossupply.dto.clickhouse;

import lombok.*;
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
@ToString
public class LogHistoryClickhouseDTO {

    String id;

    String field;

    String fromStringValue;

    BigDecimal fromNumberValue;

    String fromDateValue;

    String toStringValue;

    BigDecimal toNumberValue;

    String toDateValue;

    Long entityId;

    String entityType;

    Long updaterId;

    String updaterName;

    String updatedDate;

    Long companyId;

    String companyName;

    String companyTin;

    Long branchId;
}
