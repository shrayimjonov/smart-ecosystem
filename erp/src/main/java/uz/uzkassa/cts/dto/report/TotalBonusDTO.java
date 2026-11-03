package uz.uzkassa.cts.dto.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TotalBonusDTO implements Serializable {

    String type;

    Long total;

    Long bonus;

}
