package uz.uzkassa.horeca.dto.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OwnerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8898239L;

    Long id;

    String firstName;

    String lastName;

    String patronymic;

    String login;

    String phone;

    String tin;

    String pinfl;
}
