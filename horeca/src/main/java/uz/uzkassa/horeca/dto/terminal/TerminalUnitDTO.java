package uz.uzkassa.horeca.dto.terminal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TerminalUnitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8989874984528L;

    Long id;
    String name;
    String nameRu;
    String nameUz;
    String description;
    Long code;
    boolean countable;
}
