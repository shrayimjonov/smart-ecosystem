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
public class TerminalChangePasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 345345L;

    String currentPassword;

    String newPassword;
}
