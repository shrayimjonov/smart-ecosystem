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
@Table(name = "company_device",
        indexes = {
                @Index(columnList = "device_id", name = "company_device_device_id_idx"),
                @Index(columnList = "company_id", name = "company_device_company_id_idx"),
                @Index(columnList = "status", name = "company_device_status_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE company_device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyDevice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2349722169L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED;//Status can be REGISTERED, TRANSFERRED or DELETED
}
