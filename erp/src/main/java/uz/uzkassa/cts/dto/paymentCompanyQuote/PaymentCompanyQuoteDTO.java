package uz.uzkassa.cts.dto.paymentCompanyQuote;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PaymentCompanyQuoteDTO implements Serializable {

    @NotNull
    Integer qty;

    @NotNull
    Long modelId;

    @NotNull
    Long companyId;
}
