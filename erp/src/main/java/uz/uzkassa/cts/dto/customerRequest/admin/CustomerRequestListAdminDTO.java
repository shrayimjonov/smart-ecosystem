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
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerRequestListAdminDTO implements Serializable {

    Long id;

    String info;

    CustomerShortDTO customer;

    CustomerRequestStatus status;

    CallStatus callStatus;

    CustomerRequestType requestType;

    CommonDTO creator;

    CommonDTO lastModifier;

    LocalDateTime createdDate;

    LocalDateTime lastModifiedDate;

    LocalDateTime resolvedDate;
}
