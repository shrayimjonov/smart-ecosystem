package uz.uzkassa.smartpossupply.dto.sync.company;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityTypeSyncDTO implements Serializable {

    Long code;

    Long parentCode;
}
