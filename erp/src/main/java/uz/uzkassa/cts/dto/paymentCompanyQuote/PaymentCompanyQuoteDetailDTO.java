package uz.uzkassa.cts.dto.paymentCompanyQuote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PaymentCompanyQuoteDetailDTO implements Serializable {

    Long id;
    Long modelId;
    String modelName;
    String qty;
    String tin;
    String companyName;
    BaseAddressDetailDTO address;
    Long bankId;
    String bankName;
    String oked;
    String accountNumber;
}
