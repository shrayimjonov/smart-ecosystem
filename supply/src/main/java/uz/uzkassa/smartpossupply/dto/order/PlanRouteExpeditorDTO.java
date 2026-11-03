package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteExpeditorDTO {

    Long planRouteId;

    List<Long> expeditors;

    public PlanRouteExpeditorDTO(Long planRouteId, List<Long> expeditors) {
        this.planRouteId = planRouteId;
        this.expeditors = expeditors;
    }
}
