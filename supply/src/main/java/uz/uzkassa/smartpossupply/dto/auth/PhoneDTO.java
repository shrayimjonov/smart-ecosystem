package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.enums.Role;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneDTO implements Serializable {

    @NotNull
    @NotEmpty
    @Pattern(regexp = Constants.PHONE_REGEX)
    String phone;

}
