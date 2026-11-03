package uz.uzkassa.cts.dto.equipmentrequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.Priority;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Created date time: 25.12.2024 14:49
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentRequestDTO implements Serializable {

    String description;

    Priority priority;

    LocalDate requestDate;

    Long companyId;

    Long approverId;

    List<EquipmentRequestItemDTO> items;
}
