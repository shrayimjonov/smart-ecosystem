package uz.uzkassa.cts.dto.customerRequest;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CustomerRequestDTO implements Serializable {

    Long id;
    Long customerId;
    CustomerRequestType requestType;
    CallStatus callStatus;
    CustomerRequestStatus status;
    String info;
    String description;
}
