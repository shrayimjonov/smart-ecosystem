package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.interfaces.Auditable;
import uz.uzkassa.cts.utils.DateUtil;

import java.io.Serial;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "holiday",
        indexes = {
                @Index(columnList = "name", name = "holiday_name_idx"),
                @Index(columnList = "from_date", name = "holiday_from_date_idx"),
                @Index(columnList = "to_date", name = "holiday_to_date_idx"),
                @Index(columnList = "holiday_date", name = "holiday_date_idx"),
                @Index(columnList = "company_id", name = "holiday_company_id_idx")
        })
@Entity
@SQLDelete(sql = "UPDATE holiday SET deleted='true' WHERE id=?")
public class Holiday extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8539859328L;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "holiday_date")
    LocalDate holidayDate;

    @Column(name = "from_date")
    LocalDate fromDate;

    @Column(name = "to_date")
    LocalDate toDate;

    @Column(name = "day_off")
    boolean dayOff = true;

    @Column(name = "recurring")
    boolean recurring = false;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;

    @Override
    public Long getEntityId() {
        return getId();
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.HOLIDAY;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }

    public void setName(String name) {
        addChangeLog("name", this.name != null ? this.name : "", name);
        this.name = name;
    }

    public void setDescription(String description) {
        addChangeLog("description", this.description != null ? this.description : "", description);
        this.description = description;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        addChangeLog("holidayDate", this.holidayDate != null ? DateUtil.getLocalDateAsString(this.holidayDate) : "", DateUtil.getLocalDateAsString(holidayDate));
        this.holidayDate = holidayDate;
    }

    public void setFromDate(LocalDate fromDate) {
        addChangeLog("fromDate", this.fromDate != null ? DateUtil.getLocalDateAsString(this.fromDate) : "", DateUtil.getLocalDateAsString(fromDate));
        this.fromDate = fromDate;
    }

    public void setToDate(LocalDate toDate) {
        addChangeLog("toDate", this.toDate != null ? DateUtil.getLocalDateAsString(this.toDate) : "", DateUtil.getLocalDateAsString(toDate));
        this.toDate = toDate;
    }

    public void setStatus(CommonStatus status) {
        addChangeLog("status", this.status != null ? this.status.getName() : "", status.getName());
        this.status = status;
    }

    public void setDayOff(boolean dayOff) {
        addChangeLog("dayOff", this.dayOff ? "Yes" : "No", dayOff ? "Yes" : "No");
        this.dayOff = dayOff;
    }

    public void setRecurring(boolean recurring) {
        addChangeLog("recurring", this.recurring ? "Yes" : "No", recurring ? "Yes" : "No");
        this.recurring = recurring;
    }

    public void setCompany(Company company) {
        addChangeLog("company", this.company != null ? this.company.getName() : null, company != null ? company.getName() : null);
        this.company = company;
    }
}
