package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceStatus;

import java.io.Serial;

@Entity
@Table(name = "customer_device",
        indexes = {
                @Index(columnList = "device_id", name = "customer_device_device_id_idx"),
                @Index(columnList = "customer_id", name = "customer_device_customer_id_idx"),
                @Index(columnList = "status", name = "customer_device_status_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE customer_device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDevice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4113477168L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED; //Status can be REGISTERED, TRANSFERRED, DELETED

    @Column(name = "paid", columnDefinition = "boolean default false")
    boolean paid = false;
}
