package uz.uzkassa.smartpossupply.domain.branch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "branch_bank",
    indexes = {
        @Index(columnList = "branch_id", name = "branch_bank_branch_id_idx"),
        @Index(columnList = "bank_id", name = "branch_bank_bank_id_idx")
    })
public class BranchBank extends AbstractAuditingEntity implements Auditable {

    @Column(name = "branch_id", nullable = false)
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", updatable = false, insertable = false)
    Branch branch;

    @Column(name = "bank_id", nullable = false)
    Long bankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", updatable = false, insertable = false)
    Bank bank;

    @Column(name = "account_number", nullable = false, length = 20)
    String accountNumber;

    @Column(name = "oked", nullable = false, length = 6)
    String oked;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.BRANCH_BANK;
    }

    @Override
    public String getEntityName() {
        return bank.getName();
    }
}
