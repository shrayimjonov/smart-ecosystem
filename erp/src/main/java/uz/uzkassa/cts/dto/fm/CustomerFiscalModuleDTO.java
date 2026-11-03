package uz.uzkassa.cts.dto.fm;

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
public class CustomerFiscalModuleDTO implements Serializable {

    Long branchId;

    Long customerId;

    Long customerBranchId;

    String serialNumber;

}
