package uz.uzkassa.cts.domain.customer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "customer_request")
@Entity
@SQLDelete(sql = "UPDATE customer_request SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3458903458909L;

    @Column(name = "call_status")
    @Enumerated(EnumType.STRING)
    CallStatus callStatus = CallStatus.NOT_PROCESSED;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CustomerRequestStatus status = CustomerRequestStatus.NEW;

    @Column(name = "resolved_date")
    LocalDateTime resolvedDate;

    @Column(name = "request_type")
    @Enumerated(EnumType.STRING)
    CustomerRequestType requestType;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "info")
    String info;
}
