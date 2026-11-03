package uz.uzkassa.smartpossupply.domain.transaction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.enums.CurrencyType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TransactionStatus;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxrajon Rayimjonov
 * Date: 07.02.2023 18:56
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "transaction",
    indexes = {
        @Index(columnList = "company_id", name = "transaction_company_id_idx"),
        @Index(columnList = "transaction_number", name = "transaction_number_idx")
    })
@Entity
@SQLDelete(sql = "update transaction set deleted='true' where id=?")
@Inheritance(strategy = InheritanceType.JOINED)
public class Transaction extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 43467L;

    @Column(name = "transaction_number")
    String transactionNumber;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TransactionStatus status = TransactionStatus.DRAFT;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    CurrencyType currency;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<TransactionItem> items = new HashSet<>();

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    Company company;

    public void addItem(TransactionItem transactionItem) {
        transactionItem.setTransaction(this);
        transactionItem.setTransactionId(this.id);
        items.add(transactionItem);
    }

    @Override
    public Long getEntityId() {
        return null;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return null;
    }
}
