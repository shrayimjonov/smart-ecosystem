package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.enums.RepairTicketVisitType;
import uz.uzkassa.cts.enums.RepairType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RepairTicketDetailDTO {

    Long id;

    String repairTicketNumber;

    CustomerShortDTO customer;

    CommonDTO master;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    SerialNumberDTO device;

    String phone;

    LocalDateTime createdDate;

    CommonDTO type;

    CommonDTO createdBy;

    RepairTicketStatus status;

    CommonDTO repairStatus;

    RepairTicketVisitType visitType;

    LocalDateTime acceptedDate;

    RepairType repairType;

    List<CommonDTO> equipments;

    List<RepairTicketItemDetailDTO> items;

    String description;
}
