package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApayPayBillRequest implements Serializable {
    @NotNull
    @NotEmpty
    private String billId;
    private String confirmationKey;
    private String cardId;
    private String epsToken;
}
