package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TerminalApayAttachCheckRequest implements Serializable {
    @NotNull
    String billId;
    @NotNull
    @NotEmpty
    String checkId;
    TerminalOfdFiscalInfoDTO fiscalInfo;
}
