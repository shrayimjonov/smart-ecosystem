package uz.uzkassa.smartpos.web.rest.vm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.constants.Constants;

/**
 * A DTO representing a login change required data - current and new login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginChangeVM {
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    private String currentLogin;
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    private String newLogin;
}
