package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyDTO extends PhoneDTO {

    @Serial
    private static final long serialVersionUID = 1022426715809722737L;

    @Pattern(regexp = Constants.ACTIVATION_KEY_REGEX)
    String activationKey;
}
