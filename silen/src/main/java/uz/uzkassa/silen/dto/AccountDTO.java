package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.enumeration.UserRole;

import java.util.Set;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDTO {

    String id;

    @Size(max = 50)
    String firstName;

    @Size(max = 50)
    String lastName;

    String organizationId;

    String organizationTin;

    String organizationName;

    boolean hasChild;
    String parentId;

    Set<OrganizationType> organizationTypes;

    ProductGroup productGroup;

    Set<String> permissions;

    Set<String> gcp;

    UserRole role;

    String roleName;

    Set<String> authorities;

    String login;

    String version = "1.8.0";
}
