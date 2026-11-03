package uz.uzkassa.cts.dto.sync;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SyncBranchDeviceListDTO implements Serializable {

    Long id;

    String serialNumber;

    @Enumerated(EnumType.STRING)
    DeviceStatus status;

    String creatorLogin;

    LocalDateTime createdDate;

    String modifierLogin;

    LocalDateTime modifiedDate;

    Long branchId;
}
