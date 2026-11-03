package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing a password change required data - current and new password.
 */
@Getter
@Setter
public class PasswordChangeDTO {
    private String userId;
    private String currentPassword;
    private String newPassword;
}
