package uz.uzkassa.smartpos.integration.apay.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ApayAttachCheckRequest implements Serializable {
    @NotNull
    String billId;
    @NotNull
    @NotEmpty
    String checkId;
    OfdFiscalInfoDTO fiscalInfo;
}
