package uz.uzkassa.cts.dto.tin.integration;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OfdApplicationListDTO implements Serializable {

    Long id;

    Long ofdId;

    CommonDTO customer;

    CommonDTO customerBranch;

    CommonDTO branch;

    String tin;

    String deviceSerial;

    String fiscalModuleSerial;

    OfdStatus ofdStatus;

    LocalDateTime createdDate;

    String reason;

    String swapReason;

    String applicationFileUrl;

    ApplicationType applicationType;

    Long fiscalizationId;

}
