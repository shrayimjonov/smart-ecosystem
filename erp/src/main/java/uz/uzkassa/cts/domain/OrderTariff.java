package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order_tariff")
@SQLDelete(sql = "UPDATE order_tariff SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class OrderTariff extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3821591235L;

    @Column(name = "tariff_id")
    private Long tariffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", insertable = false, updatable = false)
    private Tariff tariff;

    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @OneToMany(mappedBy = "orderTariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceTariff> deviceTariffs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_id")
    private CtsServiceOrder serviceOrder;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    public void addChild(DeviceTariff deviceTariff) {
        deviceTariff.setOrderTariff(this);
        deviceTariffs.add(deviceTariff);
    }

}
