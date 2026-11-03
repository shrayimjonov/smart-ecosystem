package uz.uzkassa.cts.dto.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBranchDeviceTransferDTO implements Serializable {

    Long deviceId;

    Long fromCustomerBranchId;

    Long toCustomerBranchId;
}
