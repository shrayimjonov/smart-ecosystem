package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.Action;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class History extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 3951395093L;

    @Column(name = "serial_number")
    String serialNumber;

    @Column(name = "from_warehouse")
    String fromWarehouse;

    @Column(name = "to_warehouse")
    String toWarehouse;

    @Column(name = "fm", columnDefinition = "boolean default false")
    boolean fm = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    Action action;

    @Column(name = "modifier_id")
    Long modifierId;

    @Column(name = "modifier")
    String modifier;

    @Column(name = "modified_date")
    LocalDateTime modifiedDate = LocalDateTime.now();

    @Column(name = "from_value")
    String fromValue;

    @Column(name = "to_value")
    String toValue;

}
