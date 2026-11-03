package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.enums.LanguageEnum;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9112426715809722737L;

    @Pattern(regexp = Constants.PHONE_REGEX)
    String phone;

    @Size(min = 4, max = 50)
    String password;

    @Size(min = 4, max = 50)
    String passwordConfirmation;

    @Size(min = 36, max = 36)
    String secretKey;

    LanguageEnum language;
}
