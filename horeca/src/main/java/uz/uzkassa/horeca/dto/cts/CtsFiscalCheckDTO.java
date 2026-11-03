package uz.uzkassa.horeca.dto.cts;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class CtsFiscalCheckDTO implements Serializable {

    String deviceSerial;

    String fiscalModuleSerial;

    String hash;
}
