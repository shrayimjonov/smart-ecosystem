package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApayPayBillRequest implements Serializable {
    @NotNull
    @NotEmpty
    private String billId;
    private String confirmationKey;
    private String cardId;
    private String epsToken;
}
