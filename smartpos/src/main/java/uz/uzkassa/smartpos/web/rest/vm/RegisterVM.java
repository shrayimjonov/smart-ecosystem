package uz.uzkassa.smartpos.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.constants.Constants;

import java.io.Serializable;


@Getter
@Setter
public class RegisterVM implements Serializable {
    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 12, max = 12)
    private String login;
}
