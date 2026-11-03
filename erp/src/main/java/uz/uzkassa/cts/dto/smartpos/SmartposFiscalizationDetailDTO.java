package uz.uzkassa.cts.dto.smartpos;

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
public class SmartposFiscalizationDetailDTO implements Serializable {

    SmartposCustomerDetailDTO customer;

    SmartposCustomerBranchDetailDTO customerBranch;

    String deviceSerial;

    String fiscalModuleSerial;

    Long terminalId;

    String deviceType;
}
