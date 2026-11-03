package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalBranchDTO extends TerminalBaseAuditDTO {

    String name;
    String address;
    TerminalRegionDTO region;
    TerminalCityDTO city;
    //NeighborhoodDTO neighborhood;
    String description;
    boolean deleted;
    String reasonForDeletion;
    Long companyId;
    String companyName;
    boolean fiscal;
    TerminalActivityTypeDTO activityTypeDTO;
    long usersCount;
    String password;
    String serialNumber;
    LocalDateTime cashboxCreatedDate;
    List<String> terminalSerialNumbers;
    String longitude;
    String latitude;
    Long ofdId;
    //OfdStatusEnum ofdStatus;
    String ofdStatusName;
    Long rentFileId;
    String rentFileUrl;
    Long cadastreFileId;
    String cadastreFileUrl;

    //ApayRegisterStatus apayStatus;

    public TerminalBranchDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
