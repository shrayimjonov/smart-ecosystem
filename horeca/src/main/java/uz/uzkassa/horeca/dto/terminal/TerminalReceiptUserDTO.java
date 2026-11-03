package uz.uzkassa.horeca.dto.terminal;

import java.io.Serial;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalReceiptUserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 99948359L;

    Long id;

    String login;

    TerminalFullNameDTO fullName;
}
