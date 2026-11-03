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
import uz.uzkassa.cts.enums.SmartOrOldDeviceEnum;

import java.io.Serial;

@Entity
@Table(name = "trade_in_device")
@Getter
@Setter
@SQLDelete(sql = "UPDATE trade_in_device SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInDevice extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8371092357L;

    @Column(name = "name", unique = true)
    private String name;

    @NotNull
    @Column(name = "smart_or_old_device")
    @Enumerated(EnumType.STRING)
    SmartOrOldDeviceEnum smartOrOldDeviceEnum;
}
