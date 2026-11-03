package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.PaymentType;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "customer_branch_device_payment_type",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_branch_device_id", "payment_status"})
        }
)
public class CustomerBranchDevicePaymentType extends SimpleEntity {

    @NotNull
    @Column(name = "customer_branch_device_id", nullable = false)
    Long customerBranchDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_device_id", updatable = false, insertable = false)
    CustomerBranchDevice customerBranchDevice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentType paymentType = PaymentType.NOT_CONNECTED;
}
