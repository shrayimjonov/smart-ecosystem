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
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "vacancy_application", indexes = {
        @Index(columnList = "department_id", name = "vacancy_application_department_id_idx"),
        @Index(columnList = "position_id", name = "vacancy_application_position_id_idx"),
        @Index(columnList = "grade_id", name = "vacancy_application_grade_id_idx"),
        @Index(columnList = "approver_id", name = "vacancy_application_approver_id_idx"),
        @Index(columnList = "template_id", name = "vacancy_application_template_id_idx"),
        @Index(columnList = "company_id", name = "vacancy_application_company_id_idx")
})
@SQLDelete(sql = "UPDATE vacancy_application SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VacancyApplication extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 7369856987434L;

    @Column(name = "vacancy_application_number")
    String vacancyApplicationNumber;

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

    @Column(name = "qty")
    Integer qty;

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

    @Column(name = "group_id")
    Long groupId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", updatable = false, insertable = false)
    Groups group;

    @Column(name = "file_id")
    Long fileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", updatable = false, insertable = false)
    FileUpload file;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    VacancyApplicationStatus status = VacancyApplicationStatus.NEW;

    @Column(name = "approver_id")
    Long approverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", updatable = false, insertable = false)
    User approver;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "reject_reason")
    String rejectReason;

    @Column(name = "template_id")
    Long templateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", updatable = false, insertable = false)
    VacancyApplicationTemplate template;

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

    public void setQty(Integer qty) {
        addChangeLog("qty", this.qty != null ? this.qty.toString() : null, qty != null ? qty.toString() : null);
        this.qty = qty;
    }

    public void setFile(FileUpload file) {
        addChangeLog("file", this.file != null ? this.file.getName() : null, file != null ? file.getName() : null);
        this.file = file;
    }

    public void setStatus(VacancyApplicationStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : null, status != null ? status.getName() : null);
        this.status = status;
    }

    public void setTemplate(VacancyApplicationTemplate template) {
        addChangeLog("template", this.template != null ? this.template.getName() : null, template != null ? template.getName() : null);
        this.template = template;
    }

    public void setGroup(Groups group) {
        addChangeLog("group", this.group != null ? this.group.getName() : null, group != null ? group.getName() : null);
        this.group = group;
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
        return EntityTypeEnum.VACANCY_APPLICATION;
    }
}
