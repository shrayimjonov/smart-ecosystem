package uz.uzkassa.silen.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.silen.config.DbConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "billing_permission")
@Entity
@SQLDelete(sql = "UPDATE billing_permission SET deleted = 'true' WHERE id=?")
@SQLRestriction("deleted = 'false'")
public class BillingPermission extends BaseIdentityEntity {

    @Column(name = "unit_count")
    Integer unitCount;
    BigDecimal amount;
    @Column(name = "start_date")
    LocalDateTime startDate;
    @Column(name = "end_date")
    LocalDateTime endDate;
    String tin;
    @Column(name = "service_type")
    String serviceType;
    @Column(name = "note")
    String note;

    public BillingPermission(Integer unitCount, BigDecimal amount, LocalDateTime startDate, LocalDateTime endDate, String tin, String serviceType) {
        this.unitCount = unitCount;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tin = tin;
        this.serviceType = serviceType;
    }

    public BillingPermission(Integer unitCount, BigDecimal amount, LocalDateTime startDate, LocalDateTime endDate, String tin, String serviceType, String note) {
        this.unitCount = unitCount;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tin = tin;
        this.serviceType = serviceType;
        this.note = note;
    }

    public BillingPermission() {

    }
}
