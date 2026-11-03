package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ActInsuranceStatus;

import java.io.Serial;
import java.util.Objects;

@Getter
@Setter
@Table(name = "rent_device_act_detail")
@Entity
@SQLDelete(sql = "UPDATE rent_device_act_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction("deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentDeviceActDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3141362563L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_act_id")
    RentDeviceAct deviceAct;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_device_of_return_id")
    RentDeviceActOfReturn rentDeviceActOfReturn;

    @Column(name = "device_id")
    Long deviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "trade_in_price_id")
    Long tradeInPriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_price_id", insertable = false, updatable = false)
    TradeInPriceHistory tradeInPrice;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_status")
    ActInsuranceStatus insuranceStatus = ActInsuranceStatus.NOT_INSURED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentDeviceActDetail that = (RentDeviceActDetail) o;

        if (!Objects.equals(deviceId, that.deviceId))
            return false;
        return Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        int result = deviceId != null ? deviceId.hashCode() : 0;
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        return result;
    }
}
