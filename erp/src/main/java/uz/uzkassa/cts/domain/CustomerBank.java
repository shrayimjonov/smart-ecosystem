package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;

import java.io.Serial;

@Entity
@Table(name = "customer_bank", indexes = {
        @Index(columnList = "customer_id", name = "customer_bank_customer_id_idx"),
        @Index(columnList = "bank_id", name = "customer_bank_bank_id_idx"),
        @Index(columnList = "account_number", name = "customer_bank_account_number_idx"),
        @Index(columnList = "is_default", name = "customer_bank_is_default_idx")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE customer_bank SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class CustomerBank extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4398155504L;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @NotNull
    @Column(name = "bank_id", nullable = false)
    Long bankId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", updatable = false, insertable = false)
    Bank bank;

    @NotNull
    @Column(name = "account_number", nullable = false)
    String accountNumber;

    @Deprecated
    @Column(name = "oked")
    String oked;

    @Column(name = "is_default", columnDefinition = "boolean default false")
    boolean isDefault;

}
