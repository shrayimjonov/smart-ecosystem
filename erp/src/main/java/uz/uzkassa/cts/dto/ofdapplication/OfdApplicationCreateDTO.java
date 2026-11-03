package uz.uzkassa.cts.dto.ofdapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OfdApplicationCreateDTO implements Serializable {

    Long customerId;

    Long deviceId;

    Long fiscalModuleId;
}
