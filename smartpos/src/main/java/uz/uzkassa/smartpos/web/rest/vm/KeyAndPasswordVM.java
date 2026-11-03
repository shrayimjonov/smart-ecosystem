package uz.uzkassa.smartpos.web.rest.vm;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * View Model object for storing the user's key and password.
 */
@Getter
@Setter
public class KeyAndPasswordVM {
    @NotBlank(message = "Login required")
    private String login;
    private String key;
    private String newPassword;
}
