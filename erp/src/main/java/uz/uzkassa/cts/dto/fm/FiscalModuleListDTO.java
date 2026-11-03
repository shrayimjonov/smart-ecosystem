package uz.uzkassa.cts.dto.fm;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class FiscalModuleListDTO implements Serializable {

    Long id;

    String serialNumber;

    LocalDateTime registrationDate;

    DeviceStatus status;
}
