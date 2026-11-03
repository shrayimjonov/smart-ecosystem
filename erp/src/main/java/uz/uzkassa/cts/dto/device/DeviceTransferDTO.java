package uz.uzkassa.cts.dto.device;

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
public class DeviceTransferDTO implements Serializable {

    Long fromCompanyId;

    Long toCompanyId;

    Long fromBranchId;

    Long toBranchId;

    List<Long> devices;
}
