package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
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
public class ApayCardNFCRequest implements Serializable {
    @NotNull
    @NotEmpty
    String nfc;
    @NotNull
    @NotEmpty
    String pin;
    @NotBlank
    String billId;
}
