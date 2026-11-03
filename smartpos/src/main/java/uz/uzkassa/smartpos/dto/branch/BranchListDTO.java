package uz.uzkassa.smartpos.dto.branch;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BranchListDTO {
    private Long id;
    private LocalDateTime createdDate;
    private String name;
    private String address;
    private String phone;
    private String companyName;
    private Long usersCount;
    private String serialNumber;
    private Long regionId;
    private String regionName;
    private Long cityId;
    private String cityName;
    private boolean fiscal;
    private List<String> terminalSerialNumbers;
    private List<String> deviceSerialNumbers;
    private Long terminalsCount;
    private Long fiscalModulesCount;
}
