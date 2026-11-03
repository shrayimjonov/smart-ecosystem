package uz.uzkassa.cts.dto.tariff.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TariffDeviceDTO implements Serializable {

    @NotNull
    Long tariffId;

    @NotNull
    @NotEmpty
    List<Long> customerDevices;

}
