package uz.uzkassa.cts.dto.uzcard;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UzcardConnectionPayloadDTO implements Serializable {

    Long id;

    String serialNumber;
}
