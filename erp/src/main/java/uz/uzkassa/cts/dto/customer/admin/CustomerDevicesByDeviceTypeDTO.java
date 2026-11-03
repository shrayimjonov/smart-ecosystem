package uz.uzkassa.cts.dto.customer.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDevicesByDeviceTypeDTO implements Serializable {

    String tin;
    String deviceType;
    long count;
}
