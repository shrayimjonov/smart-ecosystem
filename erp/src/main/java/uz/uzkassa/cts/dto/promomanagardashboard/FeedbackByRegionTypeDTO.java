package uz.uzkassa.cts.dto.promomanagardashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class FeedbackByRegionTypeDTO {

    String region;

    List<FeedbackTypeCountDTO> typeCounts;
}
