package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.VacancyStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "vacancy",
        indexes = {
                @Index(columnList = "company_id", name = "vacancy_company_id_idx"),
                @Index(columnList = "branch_id", name = "vacancy_branch_id_idx"),
                @Index(columnList = "position_id", name = "vacancy_position_id_idx")
        })
@SQLDelete(sql = "UPDATE vacancy SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vacancy extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 358961234789954L;

    @Column(name = "vacancy_number")
    String vacancyNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @NotNull
    @Column(name = "name", nullable = false)
    String name;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "responsibility")
    String responsibility;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "requirement")
    String requirement;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "condition")
    String condition;

    @NotNull
    @Column(name = "department_id", nullable = false)
    Long departmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", updatable = false, insertable = false)
    Department department;

    @NotNull
    @Column(name = "position_id", nullable = false)
    Long positionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", updatable = false, insertable = false)
    Position position;

    @Column(name = "grade_id")
    Long gradeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", updatable = false, insertable = false)
    Reference grade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    VacancyStatus status = VacancyStatus.DRAFT;

    @Column(name = "application_id")
    Long applicationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", updatable = false, insertable = false)
    VacancyApplication application;

    @Column(name = "hr_manager_id")
    Long hrManagerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_manager_id", updatable = false, insertable = false)
    User hrManager;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setResponsibility(String responsibility) {
        addChangeLog("responsibility", this.responsibility, responsibility);
        this.responsibility = responsibility;
    }

    public void setRequirement(String requirement) {
        addChangeLog("requirement", this.requirement, requirement);
        this.requirement = requirement;
    }

    public void setCondition(String condition) {
        addChangeLog("condition", this.condition, condition);
        this.condition = condition;
    }

    public void setDepartment(Department department) {
        addChangeLog("department", this.department != null ? this.department.getName() : null, department != null ? department.getName() : null);
        this.department = department;
    }

    public void setPosition(Position position) {
        addChangeLog("position", this.position != null ? this.position.getName() : null, position != null ? position.getName() : null);
        this.position = position;
    }

    public void setGrade(Reference grade) {
        addChangeLog("grade", this.grade != null ? this.grade.getName() : null, grade != null ? grade.getName() : null);
        this.grade = grade;
    }

    public void setVacancyApplication(VacancyApplication application) {
        addChangeLog("application", this.application != null ? this.application.getName() : null, application != null ? application.getName() : null);
        this.application = application;
    }

    public void setStatus(VacancyStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setApplication(VacancyApplication application) {
        addChangeLog("application", this.application != null ? this.application.getName() : null, application != null ? application.getName() : null);
        this.application = application;
    }

    public void setBranch(Branch branch) {
        addChangeLog("branch", this.branch != null ? this.branch.getName() : null, branch != null ? branch.getName() : null);
        this.branch = branch;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.VACANCY;
    }
}
