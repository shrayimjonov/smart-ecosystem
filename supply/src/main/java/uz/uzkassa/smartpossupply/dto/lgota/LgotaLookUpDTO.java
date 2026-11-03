package uz.uzkassa.smartpossupply.dto.lgota;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.07.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LgotaLookUpDTO implements Serializable {

    Long id;

    Integer lgotaId;

    String name;

    Integer type;
}
