package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SupervisorPlanRouteDTO {
    CommonDTO planRoute;
    CommonDTO region;
    CommonDTO agent;
    List<CommonDTO> expeditor;
    int customerBranchCount;
    LocalDateTime createdDate;
}
