package uz.uzkassa.cts.dto.ofdapplication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.Reason;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SwapFiscalModuleDTO implements Serializable {

    Long applicationId;

    String fiscalModuleSerial;

    Reason reason;

    String reasonText;
}
