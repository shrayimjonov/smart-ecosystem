package uz.uzkassa.cts.dto.sync;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.enums.OfdStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SyncCustomerBranchListDTO implements Serializable {

    Long id;

    String name;

    String tin;

    String phone;

    String cadastreNumber;

    Long ofdId;

    OfdStatus ofdStatus;

    boolean hasAct;

    boolean hasInvoicePayment;

    CustomerBranchStatus status;

    Long regionCode;

    Long districtCode;

    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;

    Long activityTypeId;

    String creatorLogin;

    LocalDateTime createdDate;

    String modifierLogin;

    LocalDateTime modifiedDate;
}
