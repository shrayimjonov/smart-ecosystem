package uz.uzkassa.cts.dto.device;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.rentact.request.RentActAccessoryRequestDto;
import uz.uzkassa.cts.enums.DevicePaymentType;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerDeviceDTO implements Serializable {

    @NotNull
    Long customerId;

    List<Long> devices;

    Long contractId;

    @NotNull
    Long customerBranchId;

    boolean fromRegistry = true;

    Set<RentActAccessoryRequestDto> accessories;

    DevicePaymentType devicePaymentType;

    DeviceType deviceType;
}
