package uz.uzkassa.cts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Language;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE jhi_user SET deleted = 'true' WHERE id=?")
public class User extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8717155426L;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    String login;

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

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    Language language = Language.ru;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    @JsonIgnore
    String resetKey;

    @Column(name = "birthday")
    LocalDate birthday;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    UserStatus status = UserStatus.ACTIVE;

    @Column(name = "company_id")
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "photo_id")
    Long photoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false, insertable = false)
    FileUpload photo;

    @Column(name = "time_track_photo_id")
    Long timeTrackPhotoId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_track_photo_id", updatable = false, insertable = false)
    FileUpload timeTrackPhoto;

    @Column(name = "branch_id")
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "position_id")
    Long positionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    Position position;

    @Column(name = "department_id")
    Long departmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @Column(name = "timeslot_id")
    Long timeslotId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id", updatable = false, insertable = false)
    Timeslot timeslot;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "jhi_user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    Set<Authority> authorities = new HashSet<>();

    public void setLogin(String login) {
        addChangeLog("login", this.login, login);
        this.login = login.toLowerCase(Locale.ROOT);
    }

    public void setPassword(String password) {
        addChangeLog("password", this.password, password);
        this.password = password;
    }

    public void setFirstName(String firstName) {
        addChangeLog("firstName", this.firstName, firstName);
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        addChangeLog("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public void setPatronymic(String patronymic) {
        addChangeLog("patronymic", this.patronymic, patronymic);
        this.patronymic = patronymic;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setLanguage(Language language) {
        addChangeLog("language", this.language != null ? this.language.getName() : null, language != null ? language.getName() : null);
        this.language = language;
    }

    public void setStatus(UserStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    public void setBranch(Branch branch) {
        addChangeLog("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setPosition(Position position) {
        addChangeLog("position", this.position != null ? this.position.getName() : null, position != null ? position.getName() : null);
        this.position = position;
    }

    public void setDepartment(Department department) {
        addChangeLog("department", this.department != null ? this.department.getName() : null, department != null ? department.getName() : null);
        this.department = department;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public String getEntityName() {
        return getFullName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.EMPLOYEE;
    }

    public CommonDTO getAsCaseCommonDTO() {
        return new CommonDTO(getId(), getNameWithBranch());
    }

    private String getNameWithBranch() {
        if (getBranch() == null)
            return getName();
        return getName() + " (" + getBranch().getName() + ")";
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

    public String getName() {
        StringBuilder fullName = new StringBuilder();
        if (StringUtils.isNotEmpty(getFirstName())) {
            fullName.append(getFirstName().trim()).append(" ");
        }
        if (StringUtils.isNotEmpty(getLastName())) {
            fullName.append(getLastName().trim());
        }
        return fullName.toString().trim();
    }
}
