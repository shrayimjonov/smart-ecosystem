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

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RepairTicketListDTO {

    Long id;

    String repairTicketNumber;

    CustomerShortDTO customer;

    CommonDTO fromBranch;

    CommonDTO toBranch;

    CommonDTO currentBranch;

    SerialNumberDTO device;

    String phone;

    LocalDateTime createdDate;

    CommonDTO type;

    CommonDTO creator;

    RepairTicketStatus status;

    LocalDateTime updatedDate;

    LocalDateTime acceptedDate;

    RepairTicketVisitType visitType;

    RepairType repairType;

    CommonDTO repairStatus;

    String description;
}
