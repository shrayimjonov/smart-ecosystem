package uz.uzkassa.cts.dto.equipmentrequest.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestItemDetailDTO;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created date time: 25.12.2024 14:49
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestDetailAdminDTO implements Serializable {

    Long id;

    String equipmentRequestNumber;

    String description;

    Priority priority;

    LocalDate requestDate;

    CommonDTO company;

    CommonDTO approver;

    CommonDTO creator;

    LocalDateTime createdDate;

    List<EquipmentRequestItemDetailDTO> items;
}
