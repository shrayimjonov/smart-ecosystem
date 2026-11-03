package uz.uzkassa.cts.dto.paymentQuote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDetailDTO;
import uz.uzkassa.cts.enums.PaymentQuote;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.PaymentQuoteType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PaymentQuoteDetailDTO implements Serializable {

    Long id;
    Long modelId;
    String modelName;
    String qty;
    String tin;
    String customerName;
    BaseAddressDetailDTO address;
    String director;
    String phone;
    String description;
    PaymentQuoteStatus status;
    PaymentQuote paymentStatus;
    PaymentQuoteType paymentType;
    LocalDateTime createdDate;
    String creatorName;
}
