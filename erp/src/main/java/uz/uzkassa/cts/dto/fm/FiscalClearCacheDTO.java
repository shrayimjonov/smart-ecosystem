package uz.uzkassa.cts.dto.fm;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class FiscalClearCacheDTO implements Serializable {

    String deviceSerial;

    String fiscalModuleSerial;
}
