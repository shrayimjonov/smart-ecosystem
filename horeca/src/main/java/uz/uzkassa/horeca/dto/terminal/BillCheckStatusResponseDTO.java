package uz.uzkassa.horeca.dto.terminal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BillCheckStatusResponseDTO implements Serializable {
    String code;
    String name;
    String clientPhoneNumber;
    String message;
    LocalDateTime time;
    String epsBillId;
    BigDecimal amount;
}
