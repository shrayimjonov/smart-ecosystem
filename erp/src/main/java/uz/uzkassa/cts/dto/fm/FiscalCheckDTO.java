package uz.uzkassa.cts.dto.fm;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class FiscalCheckDTO implements Serializable {

    String deviceSerial;

    String fiscalModuleSerial;

    String hash;
}
