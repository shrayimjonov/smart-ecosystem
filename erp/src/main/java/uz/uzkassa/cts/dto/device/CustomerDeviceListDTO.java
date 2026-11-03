package uz.uzkassa.cts.dto.device;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDeviceListDTO implements Serializable {

    Long id;

    String serialNumber;

    CommonDTO model;

}
