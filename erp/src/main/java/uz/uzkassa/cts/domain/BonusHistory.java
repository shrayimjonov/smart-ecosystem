package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.BonusType;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "bonus_history")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE bonus_history SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class BonusHistory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 531448970L;

    @Column(name = "bonus", precision = 15, scale = 2)
    BigDecimal bonus;

    @Column(name = "bonus_type")
    @Enumerated(EnumType.STRING)
    BonusType bonusType;

    @Column(name = "tariff_id")
    Long tariffId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", updatable = false, insertable = false)
    Tariff tariff;

    @Column(name = "cts_service_id")
    Long ctsServiceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cts_service_id", updatable = false, insertable = false)
    CtsService ctsService;

    @Column(name = "leasing_id")
    Long leasingId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leasing_id", updatable = false, insertable = false)
    ModelPriceHistory leasing;
}
