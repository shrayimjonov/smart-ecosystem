package uz.uzkassa.cts.dto.ofd;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmCustomerBranchDTO implements Serializable {

    Long ofdId;

    Long customerId;

    Long customerBranchId;
}
