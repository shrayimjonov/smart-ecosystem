package uz.uzkassa.horeca.dto.billing;

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
public class FiscalizationSyncDTO implements Serializable {

    String deviceSerial;

    String fiscalModuleSerial;

    Long branchId;

    String tin;
}
