package uz.uzkassa.horeca.dto.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AccountProfileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 343091L;

    Long id;

    String firstName;

    String lastName;

    String patronymic;
}
