package uz.uzkassa.cts.dto.ledger.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentStatementResponseDto implements Serializable {

    BigDecimal needAmount;
    BigDecimal balance;

}
