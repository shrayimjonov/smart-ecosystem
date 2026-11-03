package uz.uzkassa.smartpos.dto.branch;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.application.OfdStatusEnum;
import uz.uzkassa.smartpos.enums.ApayRegisterStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BranchDTO extends BaseAuditDTO {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    private String address;
    private RegionDTO region;
    private CityDTO city;
    private NeighborhoodDTO neighborhood;
    private String description;
    private boolean deleted;
    private Long companyId;
    private String companyName;
    private boolean fiscal;
    private ActivityTypeDTO activityTypeDTO;
    private long usersCount;
    private String password;
    private String serialNumber;
    private LocalDateTime cashboxCreatedDate;
    private List<String> terminalSerialNumbers;
    private List<String> deviceSerialNumbers;
    private String longitude;
    private String latitude;
    private Long ofdId;
    private OfdStatusEnum ofdStatus;
    private String ofdStatusName;
    private Long rentFileId;
    private String rentFileUrl;
    private Long cadastreFileId;
    private String cadastreFileUrl;
    private ApayRegisterStatus apayStatus;


}
