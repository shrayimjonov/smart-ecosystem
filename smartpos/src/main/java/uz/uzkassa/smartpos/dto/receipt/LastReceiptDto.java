package uz.uzkassa.smartpos.dto.receipt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LastReceiptDto {
    LocalDateTime receiptDateTime;
    String fiscalUrl;
}
