package uz.uzkassa.horeca.domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.address.Address;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.shift.Shift;
import uz.uzkassa.horeca.enums.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users",
        indexes = {
                @Index(columnList = "login", name = "user_login_idx"),
                @Index(columnList = "first_name", name = "user_first_name_idx"),
                @Index(columnList = "last_name", name = "user_last_name_idx"),
                @Index(columnList = "company_id", name = "user_company_id_idx"),
                @Index(columnList = "branch_id", name = "user_branch_id_idx"),
                @Index(columnList = "department_id", name = "user_department_id_idx"),
                @Index(columnList = "role", name = "user_role_idx"),
                @Index(columnList = "status", name = "user_status_idx")
        })
@SQLDelete(sql = "UPDATE users set deleted='true' where id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 16L;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60, nullable = false)
    String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    String lastName;

    @Size(max = 50)
    @Column(name = "guest_name", length = 50)
    String guestName;

    @Size(max = 50)
    @Column(name = "patronymic", length = 50)
    String patronymic;

    @Size(min = 9, max = 9)
    @Column(name = "tin")
    String tin;

    @Size(min = 14, max = 14)
    @Column(name = "pinfl")
    String pinfl;

    @Column(name = "birth_date")
    LocalDateTime birthDate;

    @Column(name = "joining_date")
    LocalDateTime joiningDate;

    @Embedded
    Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    LanguageEnum language = LanguageEnum.ru;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;

    @Column(name = "department_id")
    Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    Role role;

    @Size(max = 12, min = 12)
    @Pattern(regexp = Constants.PHONE_REGEX)
    @Column(name = "phone", length = 12)
    String phone;

    @Column(name = "photo_id")
    Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    FileUpload photo;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    AgeGroup ageGroup;

    @Column(name = "first_visit_date")
    LocalDateTime firstVisitDate;

    @Column(name = "shift_id")
    Long shiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", insertable = false, updatable = false)
    Shift shift;


    @Override
    public String getName() {
        StringBuilder name = new StringBuilder();
        if (getFirstName() != null) {
            name.append(getFirstName()).append(" ");
        }
        if (getLastName() != null) {
            name.append(getLastName());
        }
        return name.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public String toString() {
        return ("User{" +
                "login='" + login + '\''
                + ", firstName='" + firstName
                + '\'' + ", lastName='"
                + lastName + '\''
                + ", patronymic='" + patronymic + '\'' + '}');
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.USER;
    }
}
