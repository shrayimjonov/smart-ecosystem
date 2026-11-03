package uz.uzkassa.cts.dto.customerRequest.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerRequestDetailAdminDTO implements Serializable {

    Long id;

    CustomerShortDTO customer;

    CustomerRequestStatus status;

    CallStatus callStatus;

    CustomerRequestType requestType;

    CommonDTO creator;
}
