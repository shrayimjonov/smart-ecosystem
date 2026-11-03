package uz.uzkassa.cts.dto.apay;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class APayUpdateBillStatusRequestDTO implements Serializable {
    String status;
    Long amount;
    String phoneNumber;
    String qrCodeURL;
    String terminalId;
    String fiscalSign;
    LocalDateTime transactionDate;
}
