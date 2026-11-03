package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorDTO {
    Long supervisorId;
    String supervisorFullName;
    String phoneNumber;
    Long countPlanRoutes;
    Long countAgents;
    Long countCustomers;
    Long countCustomerBranches;
}
