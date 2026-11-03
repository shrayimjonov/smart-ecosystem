package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketSettingsListDTO implements Serializable {

    Long id;

    CommonDTO product;

    CommonDTO type;

    String code1c;

    String partNumber;

    CommonDTO creator;

    LocalDateTime createdDate;
}
