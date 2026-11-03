package uz.uzkassa.cts.dto.device;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerBranchDeviceDTO implements Serializable {

    @NotNull
    Long customerBranchId;

    @NotNull
    @NotEmpty
    List<DeviceModelSharedDTO> devices;

}
