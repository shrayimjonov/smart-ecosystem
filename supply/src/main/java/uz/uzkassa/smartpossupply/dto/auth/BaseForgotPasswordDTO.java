package uz.uzkassa.smartpossupply.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseForgotPasswordDTO {

    @Size(min = 6)
    private String password;

    @Size(min = 6)
    private String confirmPassword;

    @NotNull
    private String secretKey;
}
