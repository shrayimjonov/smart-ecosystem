package uz.uzkassa.horeca.dto.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalEnumDTO implements Serializable {

    Integer id;
    String code;
    String name;
    BigDecimal amount;

    public TerminalEnumDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public TerminalEnumDTO(String code, String name, BigDecimal amount) {
        this.code = code;
        this.name = name;
        this.amount = amount;
    }

    public TerminalEnumDTO(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public TerminalEnumDTO(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
