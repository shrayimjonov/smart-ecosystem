package uz.uzkassa.horeca.dto.account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.enums.UserStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1232355L;

    boolean userExist;

    UserStatus userStatus;
}
