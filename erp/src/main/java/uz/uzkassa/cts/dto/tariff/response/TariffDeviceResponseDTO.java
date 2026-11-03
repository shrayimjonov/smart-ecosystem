package uz.uzkassa.cts.dto.tariff.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TariffDeviceResponseDTO {

    @NotNull
    Long tariffId;

    @NotNull
    @NotEmpty
    Long customerDevicesId;
}
