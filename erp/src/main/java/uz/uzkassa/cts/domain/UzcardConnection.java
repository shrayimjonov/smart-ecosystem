package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.UzcardConnectionStatus;

import java.io.Serial;

@Entity
@Table(name = "uzcard_connection")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UzcardConnection extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 3934259728L;

    @NotNull
    @Column(name = "serial_number", nullable = false)
    String serialNumber;

    @Column(name = "customer_branch_name")
    String customerBranchName;

    @Column(name = "terminal_type")
    String terminalType;

    @Column(name = "model")
    String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    UzcardConnectionStatus status;
}
