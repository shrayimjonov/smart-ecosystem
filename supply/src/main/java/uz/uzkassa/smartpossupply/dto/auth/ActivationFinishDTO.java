package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivationFinishDTO {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Constants.PHONE_REGEX)
    String phone;

    @Size(min = 6)
    private String password;

    @Size(min = 6)
    private String confirmPassword;

    private String secretKey;
}
