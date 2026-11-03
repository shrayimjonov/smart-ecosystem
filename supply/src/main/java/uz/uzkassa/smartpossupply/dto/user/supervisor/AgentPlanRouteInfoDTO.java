package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentPlanRouteInfoDTO extends AgentInfoDTO {
    Long planRouteId;
    String planRouteName;


    public AgentPlanRouteInfoDTO(Long id, String fullName, Long planRouteId, String planRouteName) {
        super(id, fullName);
        this.planRouteId = planRouteId;
        this.planRouteName = planRouteName;
    }
}
