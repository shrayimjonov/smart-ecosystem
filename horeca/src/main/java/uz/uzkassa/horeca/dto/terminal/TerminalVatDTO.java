package uz.uzkassa.horeca.dto.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalVatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 992374832L;

    Long id;
    String name;
    BigDecimal percent;
}
