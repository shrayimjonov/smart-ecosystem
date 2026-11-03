package uz.uzkassa.cts.dto.promomanagardashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackCountByRegionTypeDTO {

    String region;

    String type;

    Long count;
}
