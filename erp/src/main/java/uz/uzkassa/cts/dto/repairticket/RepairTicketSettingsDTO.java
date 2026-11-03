package uz.uzkassa.cts.dto.repairticket;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairTicketSettingsDTO implements Serializable {

    Long productId;

    Long typeId;

    String code1c;

    String partNumber;
}
