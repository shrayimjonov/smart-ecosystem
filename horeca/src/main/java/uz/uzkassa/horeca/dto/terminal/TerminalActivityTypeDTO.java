package uz.uzkassa.horeca.dto.terminal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TerminalActivityTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5753329L;

    Long id;
    String name;
    Long parentId;
}
