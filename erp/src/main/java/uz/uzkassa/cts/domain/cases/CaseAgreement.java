package uz.uzkassa.cts.domain.cases;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.billing.AgreementStatus;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "case_agreement")
public class CaseAgreement extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4578945789L;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    Long entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    EntityTypeEnum entityType;

    @Column(name = "agreement_id")
    Long agreementId;

    @Enumerated(EnumType.STRING)
    @Column(name = "agreement_status")
    AgreementStatus agreementStatus = AgreementStatus.CREATED;
}
