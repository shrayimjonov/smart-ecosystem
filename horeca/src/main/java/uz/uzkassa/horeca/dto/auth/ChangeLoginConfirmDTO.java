package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLoginConfirmDTO extends PhoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8495842L;

    @Pattern(regexp = Constants.ACTIVATION_KEY_REGEX)
    String activationKey;

    @Size(min = 36, max = 36)
    String secretKey;
}
