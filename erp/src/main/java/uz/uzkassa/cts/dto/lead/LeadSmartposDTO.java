package uz.uzkassa.cts.dto.lead;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.LeadPaymentType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LeadSmartposDTO implements Serializable {

    @NotNull
    String tin;

    String comment;

    LeadPaymentType leadPaymentType;
}
