package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_invoice")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingInvoice extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5232095115L;

    @Column(name = "date")
    LocalDateTime date;

    @Column(name = "billing_id")
    String billingInvoiceId;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;
}
