package uz.uzkassa.smartpossupply.dto.aggregation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationHistoryExportDTO implements Serializable {

    String code;

    String gtin;

    String productName;

    LocalDateTime producedDate;

    String packageType;

    String ownerName;

    String producerName;

    String status;
}
