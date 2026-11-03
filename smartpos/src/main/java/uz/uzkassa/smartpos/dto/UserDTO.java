package uz.uzkassa.smartpos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.constants.Constants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO extends BaseAuditDTO implements Serializable {
    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(max = 12)
    private String telegramLogin;
    private String password;
    private boolean activated = false;
    @Size(min = 2, max = 10)
    private String langKey;
    private Set<String> authorities;
    private List<String> permissions;
    @NotNull
    private FullNameDTO fullName;
    private boolean dismissed;
    private boolean owner;
    private boolean isSuperAdmin;
    private Long branchId;
    private Long branchGroupId;
    private String branchName;
    private Long companyId;
    private String companyName;
    private String companyInn;
    private String activationKey;
    private String resetKey;
    private String telegramName;
    private String terminalSN;
    private Long agreementId;
    private boolean warehouseEnabled;
    private String imageUrl;
    private Long imageId;
    private LocalDate dateOfBirth;
    private String personalNumber;
    private BigDecimal salary;
    private String cardNumber;

    public UserDTO(Long id) {
        this.id = id;
    }

    public Set<String> getAuthorities() {
        if (authorities == null) {
            authorities = new HashSet<>();
        }
        return authorities;
    }
}
