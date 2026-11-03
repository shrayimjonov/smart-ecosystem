package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Getter
@Setter
@Table(name = "device_tariff")
@Entity
@SQLDelete(sql = "UPDATE device_tariff SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class DeviceTariff extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 9955503679L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_tariff_id")
    private OrderTariff orderTariff;

    @NotNull
    @Column(name = "customer_branch_devices_id", nullable = false)
    private Long customerBranchDeviceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_devices_id", insertable = false, updatable = false)
    private CustomerBranchDevice customerBranchDevice;

}
