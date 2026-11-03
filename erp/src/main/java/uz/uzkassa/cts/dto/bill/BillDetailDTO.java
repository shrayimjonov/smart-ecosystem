package uz.uzkassa.cts.dto.bill;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class BillDetailDTO implements Serializable {
    Long id;
    String billId;
    APayStatus payStatus;
    String pan;
    BigDecimal amount;
    String deviceSerialNumber;
    Long customerBranchDeviceId;
    CustomerShortDTO customer;
    CommonDTO branch;
    LocalDateTime createdDate;
    String fiscalCheckUrl;
}
