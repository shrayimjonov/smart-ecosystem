package uz.uzkassa.cts.dto.sync;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SyncCustomerListDTO implements Serializable {

    Long id;

    String name;

    String tin;

    String phone;

    String status;

    String businessType;

    BigDecimal vatRate;

    Long activityTypeId;

    Long regionCode;

    Long districtCode;

    String street;

    String house;

    String apartment;

    String bankCode;

    String accountNumber;

    String oked;

    Long branchId;

    String creatorLogin;

    LocalDateTime createdDate;

    String modifierLogin;

    LocalDateTime modifiedDate;

}
