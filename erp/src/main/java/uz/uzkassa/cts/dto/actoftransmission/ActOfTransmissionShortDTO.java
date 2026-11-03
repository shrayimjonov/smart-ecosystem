package uz.uzkassa.cts.dto.actoftransmission;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created date time: 03.03.2025 13:19
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ActOfTransmissionShortDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1834728743L;

    Long id;

    String number;

    LocalDateTime createdDate;
}
