package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.Account;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Powered by: Shuxrajon Rayimjonov
 * Date: 07.02.2023 18:56
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transaction_item")
@Entity
@SQLDelete(sql = "update transaction_item set deleted='true' where id=?")
public class TransactionItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 434L;

    @Column(name = "debit", precision = 25, scale = 2)
    BigDecimal debit;

    @Column(name = "credit", precision = 25, scale = 2)
    BigDecimal credit;

    @Column(name = "transaction_id")
    Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    Transaction transaction;

    @Column(name = "account_id")
    Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    Account account;
}
