package uz.uzkassa.cts.domain.hrms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Gender;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dependent", indexes = {
        @Index(columnList = "employee_id", name = "dependent_employee_id_idx"),
        @Index(columnList = "relationship_id", name = "dependent_relationship_id_idx")
})
@SQLDelete(sql = "UPDATE dependent SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dependent extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 10876523845L;

    @NotNull
    @Column(name = "first_name", nullable = false)
    String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    String lastName;

    @NotNull
    @Column(name = "patronymic", nullable = false)
    String patronymic;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    LocalDate birthDate;

    @NotNull
    @Column(name = "relationship_id", nullable = false)
    Long relationshipId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id", updatable = false, insertable = false)
    Reference relationship;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    Long employeeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

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

    public void setGender(Gender gender) {
        addChangeLog("gender", this.gender != null ? this.gender.getName() : null, gender != null ? gender.getName() : null);
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        addChangeLog("birthDate", String.valueOf(this.birthDate), String.valueOf(birthDate));
        this.birthDate = birthDate;
    }

    public void setRelationship(Reference relationship) {
        addChangeLog("relationship", this.relationship != null ? this.relationship.getName() : null, relationship != null ? relationship.getName() : null);
        this.relationship = relationship;
    }

    public void setEmployee(User employee) {
        addChangeLog("employee", this.employee != null ? this.employee.getFullName() : null, employee != null ? employee.getFullName() : null);
        this.employee = employee;
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
        return this.id;
    }

    @Override
    public String getEntityName() {
        return getFullName();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.DEPENDENT;
    }
}