package uz.uzkassa.cts.dto.order.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.tariff.request.TariffDeviceDTO;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ServiceOrderDTO implements Serializable {

    Long id;

    @NotNull
    Long contractId;

    @NotEmpty
    List<TariffDeviceDTO> tariffDevices;

}
