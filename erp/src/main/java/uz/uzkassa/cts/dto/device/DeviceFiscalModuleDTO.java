package uz.uzkassa.cts.dto.device;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFiscalModuleDTO implements Serializable {

    String deviceSerial;

    String fiscalModuleSerial;

}
