package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordDTO extends BaseForgotPasswordDTO {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Constants.PHONE_REGEX)
    String phone;
}
