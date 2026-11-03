package uz.uzkassa.smartpossupply.domain;

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
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "competitor_product",
        indexes = {
                @Index(columnList = "customer_branch_id", name = "competitor_product_customer_branch_id_idx")
        })
public class CompetitorProduct extends AbstractAuditingEntity implements Auditable {

    @Column(name = "name")
    String name;

    @Column(name = "qty")
    Integer qty;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price;

    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.COMPETITOR_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getName();
    }
}
