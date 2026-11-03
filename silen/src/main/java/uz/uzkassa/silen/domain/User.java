package uz.uzkassa.silen.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.enumeration.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A user.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "users")
//@SQLDelete(sql = "UPDATE users SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    @Size(min = 14, max = 15)
    @Column(length = 15, unique = true)
    private String pinfl;

    @JsonIgnore
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private LocalDateTime resetDate = null;

    @Column(name = "phone")
    private String phone;

    @Column(name = "bonus", columnDefinition = "integer default 0")
    private Integer bonus;

    @Column(name = "supervisor_id")
    private String supervisorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", insertable = false, updatable = false)
    User supervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30)
    private UserRole role = UserRole.USER;

    @Column(name = "organization_tin", length = 14)
    private String organizationTin;

    @Column(name = "branch_id")
    private String branchId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    private Branch branch;

    public String getName() {
        return lastName + " " + firstName;
    }

    public UserDTO toDto() {
        UserDTO dto = new UserDTO();
        dto.setCreatedDate(getCreatedDate());
        dto.setId(getId());
        dto.setActivated(isActivated());
        dto.setOrganizationId(getOrganizationId());
        /*if (getOrganization() != null) {
            dto.setOrganizationTin(getOrganization().getTin());
            dto.setOrganizationName(getOrganization().getName());
            if (CollectionUtils.isNotEmpty(getOrganization().getTypes())) {
                dto.setOrganizationTypes(getOrganization().getTypes());
            }
        }*/
        dto.setPinfl(getPinfl());
        dto.setLogin(getLogin());
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setRole(getRole());
        if (getRole() != null) {
            dto.setRoleName(getRole().getText());
        }
        dto.setPhone(getPhone());
        dto.setBonus(getBonus());
        return dto;
    }


    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(getId(), getFirstName() + " " + getLastName());
    }
}
