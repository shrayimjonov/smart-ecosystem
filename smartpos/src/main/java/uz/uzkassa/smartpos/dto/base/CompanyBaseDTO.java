package uz.uzkassa.smartpos.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class CompanyBaseDTO implements Serializable {
    Long id;
    String name;
    String tin;
}
