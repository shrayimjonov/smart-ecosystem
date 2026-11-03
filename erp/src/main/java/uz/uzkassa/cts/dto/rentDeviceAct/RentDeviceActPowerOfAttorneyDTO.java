package uz.uzkassa.cts.dto.rentDeviceAct;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentDeviceActPowerOfAttorneyDTO implements Serializable {
    Long actId;

    Long powerOfAttorneyNumber;

    LocalDate currentDate = LocalDate.now();

    LocalDate dueDate = LocalDate.now();

    String position;

    String fullName;

    String passportSerial;

    String passportNumber;

    String issueBy;

    LocalDate issueDate;

    Integer qty;

    String qtyInWord;
}
