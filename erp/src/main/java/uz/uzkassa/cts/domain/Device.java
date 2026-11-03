package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.DeviceType;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "device", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"serial_number", "model_id"})
},
        indexes = {
                @Index(columnList = "serial_number", name = "device_serial_number_idx"),
                @Index(columnList = "model_id", name = "device_model_id_idx"),
                @Index(columnList = "status", name = "device_status_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Device extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5529654287L;

    @NotNull
    @NotEmpty
    @Column(name = "serial_number", nullable = false)
    String serialNumber;

    @Column(name = "model_id")
    Long modelId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", updatable = false, insertable = false)
    DeviceModel model;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.NOT_REGISTERED; //Status can be NOT_REGISTERED, REGISTERED, or UNREGISTERED

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    DeviceType type = DeviceType.PHYSICAL;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Override
    public String getName() {
        return getSerialNumber();
    }

    public SerialNumberDTO toSerialNumberDTO() {
        return new SerialNumberDTO(getId(), getSerialNumber());
    }
}
