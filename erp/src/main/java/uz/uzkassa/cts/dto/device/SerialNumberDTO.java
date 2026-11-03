package uz.uzkassa.cts.dto.device;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumberDTO implements Serializable {

    Long id;

    String serialNumber;

}
