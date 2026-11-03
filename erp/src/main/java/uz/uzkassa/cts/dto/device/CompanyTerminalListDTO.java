package uz.uzkassa.cts.dto.device;

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
public class CompanyTerminalListDTO implements Serializable {

    Long id;

    String serialNumber;

    String model;

    DeviceStatus status;

    LocalDateTime registrationDate;

    Long branchId;

    String branch;

    Long customerBranchId;

    String customerBranch;

}
