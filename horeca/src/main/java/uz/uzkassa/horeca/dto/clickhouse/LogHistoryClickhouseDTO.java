package uz.uzkassa.horeca.dto.clickhouse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
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
}
