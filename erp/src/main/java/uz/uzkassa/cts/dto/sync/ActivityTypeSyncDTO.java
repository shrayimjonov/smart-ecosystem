package uz.uzkassa.cts.dto.sync;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActivityTypeSyncDTO {

    Long code;

    Long parentCode;
}
