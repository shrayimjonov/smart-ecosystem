package uz.uzkassa.cts.dto.paymentCompanyContract;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentCompanyContractDTO implements Serializable {

    @NotNull
    private Long deviceId;
    @NotNull
    private Long paymentCompanyQuoteId;
    @NotNull
    private Long companyId;
}
