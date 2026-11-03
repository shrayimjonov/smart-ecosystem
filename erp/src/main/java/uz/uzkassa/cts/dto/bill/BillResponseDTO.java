package uz.uzkassa.cts.dto.bill;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.apay.APayStatus;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillResponseDTO implements Serializable {
    String billId;
    APayStatus billStatus;
    String payErrorMessage;
    String fiscalCheckUrl;
}
