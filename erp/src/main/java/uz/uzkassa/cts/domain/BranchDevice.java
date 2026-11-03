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
@Table(name = "branch_device",
        indexes = {
                @Index(columnList = "device_id", name = "branch_device_device_id_idx"),
                @Index(columnList = "branch_id", name = "branch_device_branch_id_idx"),
                @Index(columnList = "status", name = "branch_device_status_idx")
        })
@Getter
@Setter
@SQLDelete(sql = "UPDATE branch_device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BranchDevice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 5265954081L;

    @NotNull
    @Column(name = "device_id", nullable = false)
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @NotNull
    @Column(name = "branch_id", nullable = false)
    Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    DeviceStatus status = DeviceStatus.REGISTERED; //Status can be REGISTERED, TRANSFERRED, DELETED
}
