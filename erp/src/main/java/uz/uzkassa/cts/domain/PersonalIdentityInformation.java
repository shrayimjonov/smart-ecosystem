package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DriverLicenseCategory;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.PersonalIdentityType;
import uz.uzkassa.cts.interfaces.Auditable;
import uz.uzkassa.cts.utils.DateUtil;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "personal_identity_information", indexes = {
        @Index(columnList = "employee_id", name = "personal_identity_information_employee_id_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE personal_identity_information SET deleted = 'true' WHERE id=?")
public class PersonalIdentityInformation extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 9003372367682L;

    @Column(name = "employee_id", nullable = false)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "serial", length = 2)
    String serial;

    @Column(name = "number", length = 7, nullable = false)
    Integer number;

    @Column(name = "issue_date", nullable = false)
    LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    LocalDate expiryDate;

    @Column(name = "issue_by")
    String issueBy;

    @Column(name = "pinfl", length = 14)
    String pinfl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Enumerated(EnumType.STRING)
    @Column(name = "categories", columnDefinition = "text[]")
    List<DriverLicenseCategory> categories;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    PersonalIdentityType type;

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.PERSONAL_IDENTITY_INFORMATION;
    }

    public void setSerial(String serial) {
        addChangeLog("serial", this.serial != null ? this.serial : null, serial);
        this.serial = serial;
    }

    public void setNumber(Integer number) {
        addChangeLog("number", this.number != null ? this.number.toString() : null, number.toString());
        this.number = number;
    }

    public void setIssueBy(String issuedBy) {
        addChangeLog("issueBy", this.issueBy != null ? this.issueBy : null, issuedBy);
        this.issueBy = issuedBy;
    }

    public void setPinfl(String pinfl) {
        addChangeLog("pinfl", this.pinfl != null ? this.pinfl : null, pinfl);
        this.pinfl = pinfl;
    }

    public void setIssueDate(LocalDate issuedDate) {
        addChangeLog("issueDate", this.issueDate != null ? DateUtil.getLocalDateAsString(this.issueDate) : null, DateUtil.getLocalDateAsString(issuedDate));
        this.issueDate = issuedDate;
    }

    public void setExpiryDate(LocalDate expiredDate) {
        addChangeLog("expiryDate", this.expiryDate != null ? DateUtil.getLocalDateAsString(this.expiryDate) : null, DateUtil.getLocalDateAsString(expiredDate));
        this.expiryDate = expiredDate;
    }
}
