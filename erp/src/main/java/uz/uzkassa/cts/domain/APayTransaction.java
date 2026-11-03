package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Getter
@Setter
@Table(name = "apay_transaction")
@Entity
@SQLDelete(sql = "UPDATE apay_transaction SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class APayTransaction extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1931640275L;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    private Long amount;
    private String tin;
    private String pan;
}
