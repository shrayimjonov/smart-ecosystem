package uz.uzkassa.cts.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StatisticsByActivityTypeReportDTO implements Serializable {

    CommonDTO category;

    CommonDTO activityType;

    List<CustomerTotalByRegionDTO> items = new ArrayList<>();

    @JsonIgnore

    public void addItem(CustomerTotalByRegionDTO item) {
        items.add(item);
    }
}
