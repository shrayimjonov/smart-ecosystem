package uz.uzkassa.cts.dto.uzcard.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.UzcardConnectionStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UzcardConnectionListAdminDTO implements Serializable {

    Long id;

    String serialNumber;

    String customerBranchName;

    String terminalType;

    String model;

    boolean deleted;

    UzcardConnectionStatus status;

    LocalDateTime createdDate;

    LocalDateTime updatedDate;
}
