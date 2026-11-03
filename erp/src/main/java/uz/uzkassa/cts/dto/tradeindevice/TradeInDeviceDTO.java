package uz.uzkassa.cts.dto.tradeindevice;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TradeInDeviceDTO implements Serializable {

    Long id;

    @NotNull
    String name;

    @NotNull
    SmartOrOldDeviceEnum smartOrOldDeviceEnum;
}
