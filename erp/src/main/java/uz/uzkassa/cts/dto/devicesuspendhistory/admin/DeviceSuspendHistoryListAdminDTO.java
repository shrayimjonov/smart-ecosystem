package uz.uzkassa.cts.dto.devicesuspendhistory.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created date time: 03.03.2025 15:16
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceSuspendHistoryListAdminDTO implements Serializable {

    String deviceSerialNumber;

    LocalDate dueDate;

    LocalDate startDate;

    CommonDTO customer;

    CommonDTO tariff;

    CommonDTO creator;

    CommonDTO lastModifier;
}
