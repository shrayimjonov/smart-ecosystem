package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApayCardNFCRequest implements Serializable {
    @NotNull
    @NotEmpty
    private String nfc;
    @NotNull
    @NotEmpty
    private String pin;
    @NotBlank
    private String billId;
}
