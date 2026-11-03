package uz.uzkassa.cts.dto.fm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FiscalModuleForTransferListDTO implements Serializable {

    Long id;

    String serialNumber;

    Long fiscalModuleId;

    DeviceStatus status;
}
