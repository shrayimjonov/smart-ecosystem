package uz.uzkassa.cts.dto.fm;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class FiscalModuleDTO implements Serializable {

    String serialNumber;
}
