package uz.uzkassa.cts.dto.device;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class DeviceModelSharedDTO implements Serializable {

    @NotNull
    String serialNumber;

    @NotNull
    Long modelId;

    @NotNull
    Long companyId;
}
