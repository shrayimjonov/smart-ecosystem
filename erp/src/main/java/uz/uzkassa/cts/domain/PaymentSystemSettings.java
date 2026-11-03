package uz.uzkassa.cts.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.PaymentSystem;

import jakarta.persistence.*;

import java.io.Serial;

@Getter
@Setter
@Table(name = "payment_system_settings")
@Entity(name = "payment_system_settings")
@SQLDelete(sql = "UPDATE payment_system_settings SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class PaymentSystemSettings extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 116112988L;

    @Column(name = "payment_system")
    @Enumerated(EnumType.STRING)
    private PaymentSystem paymentSystem;
}
