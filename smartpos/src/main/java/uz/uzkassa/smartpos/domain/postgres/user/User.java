package uz.uzkassa.smartpos.domain.postgres.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.base.SimpleEntity;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.BranchGroup;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.enums.AuditedFields;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.integration.apay.OwnerDTO;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A user.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@DynamicUpdate
//@org.springframework.data.elasticsearch.annotations.Document(indexName = "user")
public class User extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 6, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Setter
    @Size(max = 50)
    @Column(name = "patronymic", length = 50)
    private String patronymic;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(max = 12)
    @Column(name = "telegram_login", length = 12, unique = true)
    private String telegramLogin;

    @Column(name = "telegram_name")
    private String telegramName;

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @NotNull
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean activated = false;

    @Column(columnDefinition = "boolean default false")
    private boolean owner = false;

    @Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    private String langKey = "ru";

    @Setter
    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Setter
    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    private String resetKey;

    @Column(name = "reset_date")
    private LocalDateTime resetDate = null;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_date")
    private LocalDateTime refreshDate = null;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<Authority> authorities = new HashSet<>();

    @Column(name = "deleted", columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Setter
    @Column(name = "branch_id")
    private Long branchId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Branch branch;

    @Column(name = "branch_group_id")
    private Long branchGroupId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_group_id", updatable = false, insertable = false)
    private BranchGroup group;

    @Column(name = "company_id")
    private Long companyId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Company company;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<TelegramAccount> telegramAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("has_access = 'true'")
    List<UserPermission> userAllowedPermissions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserPermission> userPermissions = new ArrayList<>();

    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    @Column(name = "failed_attempts")
    private Integer failedAttempts = 0;

    @OneToMany(mappedBy = "manager")
    List<Company> companies = new ArrayList<>();
    @Column(name = "latest_terminal")
    private String latestTerminal;

    @Column(name = "image_id")
    Long imageId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", updatable = false, insertable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    FileUpload userImage;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "personal_number")
    private String personalNumber;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "card_number")
    private String cardNumber;

    public List<Permission> getPermissions() {
        return userPermissions.stream().map(UserPermission::getPermission).collect(Collectors.toList());
    }

    public void setFirstName(String firstName) {
        if (!Objects.equals(this.firstName, firstName)) {
            addLog(AuditedFields.FIRSTNAME, this.firstName, firstName, companyId);
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (!Objects.equals(this.lastName, lastName)) {
            addLog(AuditedFields.LASTNAME, this.lastName, lastName, companyId);
        }
        this.lastName = lastName;
    }

    public void setLogin(String login) {
        if (!Objects.equals(this.login, login)) {
            addLog(AuditedFields.LOGIN, this.login, login, companyId);
        }
        this.login = login;
    }

    public void setAuthorities(Set<Authority> authorities) {
        if (this.getAuthorities() != null && authorities != null && !CollectionUtils.disjunction(this.authorities, authorities).isEmpty()) {
            addLog(AuditedFields.ROLE, this.authorities, authorities, companyId);
        }
        this.authorities = authorities;
    }

    public void setOwner(boolean owner) {
        if (this.owner != owner) {
            addLog(AuditedFields.OWNER, this.owner ? "Да" : "Нет", owner ? "Да" : "Нет", companyId);
        }
        this.owner = owner;
    }

    public void setPassword(String password) {
        if (!Objects.equals(this.password, password)) {
            addLog(AuditedFields.PASSWORD, this.password, password, companyId);
        }
        this.password = password;
    }

    public UserDTO getDTO() {
        final UserDTO userDTO = setAuditValues(new UserDTO());

        userDTO.setLogin(getLogin());
        userDTO.setOwner(isOwner());
        userDTO.setActivated(isActivated());
        userDTO.setFullName(getFullName());
        userDTO.setLangKey(getLangKey());
        userDTO.setDismissed(isDeleted());
        userDTO.setBranchId(getBranchId());
        userDTO.setBranchGroupId(getBranchGroupId());
        userDTO.setCompanyId(getCompanyId());
        userDTO.setDateOfBirth(getDateOfBirth());
        userDTO.setPersonalNumber(getPersonalNumber());
        userDTO.setSalary(getSalary());
        userDTO.setCardNumber(getCardNumber());
        if (getBranch() != null) {
            userDTO.setBranchName(getBranch().getName());
        }
        if (getCompany() != null) {
            userDTO.setCompanyName(getCompany().getName());
            userDTO.setWarehouseEnabled(true);
        }
        if (CollectionUtils.isNotEmpty(getAuthorities())) {
            Set<String> authorities = new HashSet<>();
            for (Authority authority : getAuthorities()) {
                if (AuthoritiesConstants.ADMIN.equals(authority.getName())) {
                    userDTO.setSuperAdmin(true);
                }
                if (!AuthoritiesConstants.USER.equals(authority.getName()) && !AuthoritiesConstants.ADMIN.equals(authority.getName())) {
                    authorities.add(authority.getName());
                }
            }
            userDTO.setAuthorities(authorities);
        }
        if (CollectionUtils.isNotEmpty(getUserAllowedPermissions())) {
            List<String> permissions = new ArrayList<>();
            for (UserPermission userPermission : getUserAllowedPermissions()) {
                permissions.add(userPermission.getPermissionCode());
            }
            userDTO.setPermissions(permissions);
        }
        if (CollectionUtils.isNotEmpty(getTelegramAccounts())) {
            getTelegramAccounts().stream().findFirst().ifPresent(e -> {
                userDTO.setTelegramLogin(e.getPhone());
                userDTO.setTelegramName(e.getName());
            });
        }
        userDTO.setTerminalSN(getLatestTerminal());
        if (getUserImage() != null) {
            userDTO.setImageUrl(getUserImage().getUrl());
        }
        return userDTO;
    }

    public UserDTO getListDTO(boolean forAdmin) {
        UserDTO dto = forAdmin ? setAuditValues(new UserDTO()) : new UserDTO();
        dto.setId(getId());
        dto.setLogin(getLogin());
        dto.setLangKey(getLangKey());
        dto.setActivated(isActivated());
        dto.setOwner(isOwner());
        dto.setFullName(getFullName());
        dto.setBranchId(getBranchId());
        dto.setDismissed(isDeleted());
        if (getBranch() != null) {
            dto.setBranchName(getBranch().getName());
            if (forAdmin && getBranch().getCompany() != null) {
                dto.setCompanyId(getBranch().getCompanyId());
                dto.setCompanyName(getBranch().getCompany().getName());
                dto.setCompanyInn(getBranch().getCompany().getInn());
            }
        }
        if (CollectionUtils.isNotEmpty(getAuthorities())) {
            dto.setAuthorities(getAuthorities().stream()
                    .map(Authority::getName)
//                    .filter(authority -> !AuthoritiesConstants.SMARTPOS_ADMIN.equals(authority.getName()))
                    .filter(name -> !AuthoritiesConstants.USER.equals(name))
                    .collect(Collectors.toSet()));
        }
        if (forAdmin) {
            dto.setActivationKey(getActivationKey());
            dto.setResetKey(getResetKey());
        }
        return dto;
    }


    public UserDTO getShortDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getId());
        userDTO.setFullName(getFullName());
        return userDTO;
    }

    public OwnerDTO toOwnerDTO() {
        OwnerDTO dto = new OwnerDTO();
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setMiddleName(getPatronymic());
        dto.setPhone(getLogin());
        dto.setLogin(getLogin());
        return dto;
    }

    public FullNameDTO getFullName() {
        return new FullNameDTO(getFirstName(), getLastName(), getPatronymic());
    }

    public String getFullNameStr() {

        StringBuilder fullName = new StringBuilder();

        if (StringUtils.isNoneBlank(getFirstName())) {
            fullName.append(getFirstName()).append(" ");
        }
        if (StringUtils.isNoneBlank(getLastName())) {
            fullName.append(getLastName()).append(" ");
        }
        if (StringUtils.isNoneBlank(getPatronymic())) {
            fullName.append(getPatronymic());
        }

        return fullName.toString().trim();
    }

    public UserDTO getAccountDTO() {
        UserDTO dto = setAuditValues(new UserDTO());
        dto.setFullName(getFullName());
        dto.setLogin(getLogin());
        dto.setActivated(isActivated());
        dto.setLangKey(getLangKey());
        if (CollectionUtils.isNotEmpty(getAuthorities())) {
            Set<String> authorities = new HashSet<>();
            for (Authority authority : getAuthorities()) {
                if (AuthoritiesConstants.ADMIN.equals(authority.getName())) {
                    dto.setSuperAdmin(true);
                }
                authorities.add(authority.getName());
            }
            dto.setAuthorities(authorities);
        }
        if (CollectionUtils.isNotEmpty(getUserAllowedPermissions())) {
            List<String> permissions = new ArrayList<>();
            for (UserPermission userPermission : getUserAllowedPermissions()) {
                permissions.add(userPermission.getPermissionCode());
            }
            dto.setPermissions(permissions);
        }
        return dto;
    }

    @Override
    public String getName() {
        return lastName + " " + firstName;
    }

    @Override
    public EntityType getHistoryType() {
        return EntityType.USER;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.getId(), this.getFullNameStr());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return getId() != null && getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", activated='" + activated + '\'' +
                ", langKey='" + langKey + '\'' +
                ", activationKey='" + activationKey + '\'' +
                "}";
    }
}
