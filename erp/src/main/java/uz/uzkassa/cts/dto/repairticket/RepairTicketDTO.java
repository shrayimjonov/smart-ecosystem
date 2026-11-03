package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RepairTicketDTO {

    Long deviceId;

    String serialNumber;

    Long customerId;

    String phone;

    Long typeId;

    String description;

    List<Long> equipments;

    List<RepairTicketItemDTO> items;
}
