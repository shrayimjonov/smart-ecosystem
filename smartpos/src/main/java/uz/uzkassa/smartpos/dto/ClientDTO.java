package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.enums.ApplicationStatus;

@Getter
@Setter
public class ClientDTO {
    private Long id;
    private String name;
    private String inn;
    private ApplicationStatus status;
    private String activityTypes;
    private String phone;
    private Long branchCount;
    private Long usersCount;
    private Long deviceTotal;
    private Long fiscalModuleTotal;
}
