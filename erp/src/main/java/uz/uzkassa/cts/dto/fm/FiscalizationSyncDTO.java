package uz.uzkassa.cts.dto.fm;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FiscalizationSyncDTO implements Serializable {

    Long fiscalizationId;
}
