package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;

@Entity
@Table(name = "warehouse_history")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1948258389L;

    @Column(name = "entity_id")
    Long entityId;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "entity_name")
    String entityName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    DeviceStatus status;

}
