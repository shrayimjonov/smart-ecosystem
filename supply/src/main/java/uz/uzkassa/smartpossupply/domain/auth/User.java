package uz.uzkassa.smartpossupply.domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.base.UserCommonDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users",
        indexes = {
                @Index(columnList = "login", name = "users_login_idx"),
                @Index(columnList = "company_id", name = "users_company_id_idx"),
                @Index(columnList = "role", name = "users_role_idx"),
        })
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
@SqlResultSetMappings(
        {
                @SqlResultSetMapping(
                        name = "ExpeditorListDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = ExpeditorListDTO.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "firstName", type = String.class),
                                        @ColumnResult(name = "lastName", type = String.class),
                                        @ColumnResult(name = "patronymic", type = String.class),
                                        @ColumnResult(name = "planRoutes", type = String.class),
                                        @ColumnResult(name = "orderCount", type = Long.class),
                                        @ColumnResult(name = "completedOrderCount", type = Long.class),
                                        @ColumnResult(name = "incomingOrderProductCount", type = Long.class),
                                        @ColumnResult(name = "outgoingOrderProductCount", type = Long.class),
                                        @ColumnResult(name = "remainderOrderProductCount", type = Long.class)
                                }
                        )
                ),
                @SqlResultSetMapping(
                        name = "ExpeditorStatisticsDTOMapper",
                        classes = @ConstructorResult(
                                targetClass = ExpeditorStatisticsDTO.class,
                                columns = {
                                        @ColumnResult(name = "expeditorTotal", type = Long.class),
                                        @ColumnResult(name = "planRouteTotal", type = Long.class),
                                        @ColumnResult(name = "orderTotal", type = Long.class),
                                        @ColumnResult(name = "completedOrderTotal", type = Long.class)
                                }
                        )
                )
        }
)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 16L;

    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50)
    String login;

    @Column(name = "certificate_name")
    String certificateName;

    @JsonIgnore
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60)
    String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    String lastName;

    @Size(max = 50)
    @Column(name = "patronymic", length = 50)
    String patronymic;

    @Size(min = 9, max = 9)
    @Column(name = "tin")
    String tin;

    @Size(min = 14, max = 14)
    @Column(name = "pinfl")
    String pinfl;

    @Embedded
    Address address;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @Column(name = "activation_key")
    @JsonIgnore
    String activationKey;

    @Column(name = "secret_key")
    String secretKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    LanguageEnum language = LanguageEnum.ru;

    @Column(name = "supervisor_id")
    Long supervisorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", updatable = false, insertable = false)
    User supervisor;

    @Column(name = "warehouse_id")
    Long warehouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", updatable = false, insertable = false)
    Warehouse warehouse;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @ManyToMany
    @JoinTable(
            name = "user_branch",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "branch_id", referencedColumnName = "id")})
    Set<Branch> branches = new HashSet<>();

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Size(max = 12, min = 12)
    @Pattern(regexp = Constants.PHONE_REGEX)
    @Column(name = "phone", length = 12)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    Role role;

    @Column(name = "telegram_user_id")
    Long telegramUserId;

    @Column(name = "refresh_token")
    String refreshToken;

    @Column(name = "refresh_date")
    LocalDateTime refreshDate;

    // Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
    }

    @Override
    public String getName() {
        return StringUtils.isNotEmpty(firstName) ? firstName.concat(" ").concat(Objects.toString(lastName, "")) : lastName;
    }

    public LanguageEnum getLanguage() {
        return Optional.ofNullable(language).orElse(LanguageEnum.ru);
    }

    public UserCommonDTO toUserCommonDTO() {
        return new UserCommonDTO(getId(), getFullName(), getRole());
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (StringUtils.isNotEmpty(getFirstName())) {
            fullName.append(getFirstName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getLastName())) {
            fullName.append(getLastName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getPatronymic())) {
            fullName.append(getPatronymic().trim());
        }
        return fullName.toString().trim();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.USER;
    }

    @Override
    public String getEntityName() {
        return getName();
    }

    public void setStatus(UserStatus status) {
        addLogHistory("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setPhone(String phone) {
        addLogHistory("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setFirstName(String firstName) {
        addLogHistory("firstName", this.firstName, firstName);
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        addLogHistory("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public void setPatronymic(String patronymic) {
        addLogHistory("patronymic", this.patronymic, patronymic);
        this.patronymic = patronymic;
    }

    public void setTin(String tin) {
        addLogHistory("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setPinfl(String pinfl) {
        addLogHistory("pinfl", this.pinfl, pinfl);
        this.pinfl = pinfl;
    }

    public void setAddress(Address address) {
        addAddressLogHistory(this.address, address);
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", companyId='" + companyId + '\'' +
                "}";
    }
}
