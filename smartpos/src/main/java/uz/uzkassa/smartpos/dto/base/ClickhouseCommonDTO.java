package uz.uzkassa.smartpos.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ClickhouseCommonDTO {

    String id;

    String name;
}
