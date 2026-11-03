package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Table(name = "act_of_transmission_detail")
@Entity
@SQLDelete(sql = "UPDATE act_of_transmission_detail SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ActOfTransmissionDetail extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5664490651L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id")
    private ActOfTransmission actOfTransmission;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "vat_for_1_pc")
    private BigDecimal vatFor1pc;

    @Column(name = "price_vith_vat_for_1_pc")
    private BigDecimal priceWithVatFor1pc;

    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @OneToMany(mappedBy = "actOfTransmissionDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("paymentDate ASC")
    private List<ActPaymentSchedule> actPaymentSchedules = new ArrayList<>();

    public ActPaymentSchedule getLastSchedule() {
        return Collections.max(actPaymentSchedules, Comparator.comparing(ActPaymentSchedule::getPaymentDate));
    }

    public ActPaymentSchedule getFirstSchedule() {
        return Collections.min(actPaymentSchedules, Comparator.comparing(ActPaymentSchedule::getPaymentDate));
    }

    public void addSchedule(ActPaymentSchedule schedule) {
        schedule.setActOfTransmissionDetail(this);
        actPaymentSchedules.add(schedule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ActOfTransmissionDetail device = (ActOfTransmissionDetail) o;
        return deviceId.equals(device.deviceId) && this.isDeleted() == device.isDeleted();
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(deviceId) + (deviceId != null ? deviceId.hashCode() : 0);
    }
}
