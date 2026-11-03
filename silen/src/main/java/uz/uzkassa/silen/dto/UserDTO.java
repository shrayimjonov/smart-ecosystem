package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO extends BaseUserDTO {

    @Size(min = 4, max = 100)
    protected String password;

    @Size(min = 4, max = 100)
    protected String passwordNew;

    protected boolean activated;

    protected LocalDateTime createdDate;

    private String organizationId;

    private String organizationTin;

    private String organizationName;

    private Set<OrganizationType> organizationTypes;

    @NotNull
    protected UserRole role = UserRole.USER;

    protected String roleName;

    String phone;

    Integer bonus;
}
