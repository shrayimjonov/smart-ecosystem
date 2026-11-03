package uz.uzkassa.cts.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PaymentContractDTO implements Serializable {

    @NotNull
    private Long paymentQuoteId;
    @NotNull
    private Long customerId;
    @NotNull
    private List<Long> deviceIds;
    @NotNull
    private Long tradeInPriceHistoryId;
    @JsonIgnore
    private Long paymentContractId;
    boolean isIndividualContract = false;
    String individualContractNumber;
    LocalDateTime individualDate;
}
