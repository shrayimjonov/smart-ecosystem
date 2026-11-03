package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Table(name = "rent_act_accessory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"customer_id", "device_serial_number", "is_active"})
        })
@Entity
@SQLDelete(sql = "UPDATE rent_act_accessory SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class RentActAccessory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3768191819L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id")
    private RentAct act;

    @Column(name = "device_serial_number")
    private String deviceSerialNumber;

    @Column(name = "accessory_id")
    private Long accessoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessory_id", insertable = false, updatable = false)
    private Accessory accessory;

    @Column(name = "is_active", columnDefinition = "boolean default false")
    private boolean active = false;

    @Column(name = "act_of_return_id")
    private Long actOfReturnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_of_return_id", insertable = false, updatable = false)
    private RentActOfReturn actOfReturn;

    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "due_date")
    LocalDate dueDate = LocalDate.now();

    @Column(name = "start_date")
    LocalDate startDate = LocalDate.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentActAccessory that = (RentActAccessory) o;

        if (!Objects.equals(deviceSerialNumber, that.deviceSerialNumber))
            return false;
        return Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        int result = deviceSerialNumber != null ? deviceSerialNumber.hashCode() : 0;
        result = 31 * result + (customerId != null ? customerId.hashCode() : 0);
        return result;
    }
}
