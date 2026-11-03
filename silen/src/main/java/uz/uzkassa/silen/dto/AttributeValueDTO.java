package uz.uzkassa.silen.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeValueDTO {
    String name;
    String prefix;
    String value;
    String postfix;
}
