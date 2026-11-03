package uz.uzkassa.horeca.dto.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalRegionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8989835L;

    Long id;
    Long code;
    String nameRu;
    String nameUz;
}
