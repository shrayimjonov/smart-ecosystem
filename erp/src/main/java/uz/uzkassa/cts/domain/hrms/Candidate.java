package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "candidate", indexes = {
        @Index(columnList = "phone", name = "candidate_phone_idx"),
        @Index(columnList = "email", name = "candidate_email_idx"),
        @Index(columnList = "position_id", name = "candidate_position_id_idx"),
        @Index(columnList = "department_id", name = "candidate_department_id_idx"),
        @Index(columnList = "vacancy_id", name = "candidate_vacancy_id_idx"),
        @Index(columnList = "status", name = "candidate_status_idx")
})
@SQLDelete(sql = "UPDATE candidate SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Candidate extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9778956565L;

    @Column(name = "candidate_number")
    String candidateNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @NotNull
    @Column(name = "first_name", nullable = false)
    String lastName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    String firstName;

    @NotNull
    @Column(name = "patronymic", nullable = false)
    String patronymic;

    @NotNull
    @Column(name = "phone", nullable = false)
    String phone;

    @Email
    @Column(name = "email")
    String email;

    @NotNull
    @Column(name = "position_id", nullable = false)
    Long positionId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    Position position;

    @NotNull
    @Column(name = "department_id", nullable = false)
    Long departmentId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @NotNull
    @Column(name = "vacancy_id", nullable = false)
    Long vacancyId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "vacancy_id", updatable = false, insertable = false)
    Vacancy vacancy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    CandidateStatus status = CandidateStatus.NEW;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void setLastName(String lastName) {
        addChangeLog("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        addChangeLog("firstName", this.firstName, firstName);
        this.firstName = firstName;
    }

    public void setPatronymic(String patronymic) {
        addChangeLog("patronymic", this.patronymic, patronymic);
        this.patronymic = patronymic;
    }

    public void setPhone(String phone) {
        addChangeLog("phone", this.phone, phone);
        this.phone = phone;
    }

    public void setEmail(String email) {
        addChangeLog("email", this.email, email);
        this.email = email;
    }

    public void setDepartment(Department department) {
        addChangeLog("department", this.department != null ? this.department.getName() : null, department != null ? department.getName() : null);
        this.department = department;
    }

    public void setPosition(Position position) {
        addChangeLog("position", this.position != null ? this.position.getName() : null, position != null ? position.getName() : null);
        this.position = position;
    }

    public void setVacancy(Vacancy vacancy) {
        addChangeLog("vacancy", this.vacancy != null ? this.vacancy.getName() : null, vacancy != null ? vacancy.getName() : null);
        this.vacancy = vacancy;
    }

    public void setStatus(CandidateStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
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
    public String getName() {
        return getFirstName().concat(" ").concat(getLastName());
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
        return EntityTypeEnum.CANDIDATE;
    }
}