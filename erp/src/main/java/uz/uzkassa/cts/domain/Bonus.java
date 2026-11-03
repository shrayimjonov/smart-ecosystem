package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bonus")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bonus extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1132660414L;

    @Column(name = "employee_id")
    Long employeeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    User employee;

    LocalDateTime startDate;

    LocalDateTime endDate;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Column(name = "company_id")
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @NotNull
    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @NotNull
    @Column(name = "bonus_amount", nullable = false)
    BigDecimal bonusAmount;

    @Column(name = "tariff_type")
    String tariffType;

    @Column(name = "app_type")
    @Enumerated(EnumType.STRING)
    CustomerAppType appType;

    @Column(name = "entity_id")
    Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    EntityTypeEnum entityType;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "billing_id")
    String billingId;
}
