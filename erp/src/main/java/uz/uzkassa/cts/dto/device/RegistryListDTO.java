package uz.uzkassa.cts.dto.device;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RegistryListDTO implements Serializable {

    Long id;

    String deviceSerial;

    String fiscalModuleSerial;

    Long branchId;

    String branch;

    Long customerId;

    String customer;

    String tin;

    Long customerBranchId;

    String customerBranch;

    String deviceStatus;

    String status;

    LocalDateTime registrationDate;

    LocalDateTime fiscalizationDate;

}
