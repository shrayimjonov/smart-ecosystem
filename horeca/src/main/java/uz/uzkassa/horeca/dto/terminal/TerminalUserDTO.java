package uz.uzkassa.horeca.dto.terminal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.constants.Constants;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TerminalUserDTO extends TerminalBaseAuditDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 45656733L;

//    @NotBlank
    @Pattern(regexp = Constants.PHONE_REGEX)
    @Size(min = 1, max = 50)
    String login;

    @Pattern(regexp = Constants.PHONE_REGEX)
    @Size(max = 12)
    String telegramLogin;

    String password;
    boolean activated = false;

    @Size(min = 2, max = 10)
    String langKey;

    Set<String> authorities;
    List<String> permissions;

//    @NotNull
    TerminalFullNameDTO fullName;

    boolean dismissed;
    boolean owner;
    boolean isSuperAdmin;
    Long branchId;
    String branchName;
    Long companyId;
    String companyName;
    String companyInn;
    boolean companyCommitent;
    String activationKey;
    String resetKey;
    String telegramName;
    String terminalSN;
    Long agreementId;
    boolean warehouseEnabled;
    String imageUrl;

    public Set<String> getAuthorities() {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        return authorities;
    }
}
