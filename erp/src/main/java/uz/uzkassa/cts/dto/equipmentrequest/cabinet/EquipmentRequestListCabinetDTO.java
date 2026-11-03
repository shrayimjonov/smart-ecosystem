package uz.uzkassa.cts.dto.equipmentrequest.cabinet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created date time: 25.12.2024 14:49
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestListCabinetDTO implements Serializable {

    Long id;

    String equipmentRequestNumber;

    String description;

    ApproverCommonStatus status;

    Priority priority;

    LocalDate requestDate;

    CommonDTO approver;

    CommonDTO creator;

    LocalDateTime createdDate;
}
