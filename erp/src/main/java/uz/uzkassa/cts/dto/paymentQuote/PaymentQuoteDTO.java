package uz.uzkassa.cts.dto.paymentQuote;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.enums.PaymentQuoteType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PaymentQuoteDTO implements Serializable {

    //    @NotNull
    @Max(value = 100, message = "Количество должно быть в диапазоне от 0 до 100!")
    Integer qty = 1;

    @NotNull
    Long modelId;

    @NotNull
    @NotEmpty
    @Size(max = 14, min = 9)
    String tin;

    @NotNull
    @NotEmpty
    String customerName;

    BaseAddressDTO address;

    @NotNull
    @NotEmpty
    String director;
    String phone;
    String description;

    @NotNull
    PaymentQuoteType paymentType;
}
