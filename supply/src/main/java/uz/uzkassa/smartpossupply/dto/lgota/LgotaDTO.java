package uz.uzkassa.smartpossupply.dto.lgota;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LgotaDTO implements Serializable {

    Long id;

    Integer lgotaId;

    Integer newLgotaId;

    String name;

    Integer type;

    String catalogCode;
}
