package uz.uzkassa.smartpossupply.dto.aggregationcodes;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Created time: 20.05.2025 15:59
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationCodeStatisticsItemDTO {

    Long total;

    String codeType;
}
