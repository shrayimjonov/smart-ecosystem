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
public class TerminalCityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4783473L;

    Long id;
    Long code;
    Long regionId;
    String nameRu;
    String nameUz;
}
