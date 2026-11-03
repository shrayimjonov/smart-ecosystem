package uz.uzkassa.horeca.dto.auth;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class PhoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1012426715809722737L;

    @Pattern(regexp = Constants.PHONE_REGEX)
    String phone;
}
