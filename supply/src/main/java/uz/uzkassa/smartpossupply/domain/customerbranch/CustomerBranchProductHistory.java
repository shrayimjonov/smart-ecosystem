package uz.uzkassa.smartpossupply.domain.customerbranch;

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
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "customer_branch_product_history",
    indexes = {
        @Index(columnList = "plan_route_customer_branch_id", name = "customer_branch_product_his_plan_route_customer_branch_id_idx"),
        @Index(columnList = "product_id", name = "customer_branch_product_his_product_id_idx")
    })
public class CustomerBranchProductHistory extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 8958875669L;

    @Column(name = "plan_route_customer_branch_id")
    Long planRouteCustomerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_customer_branch_id", updatable = false, insertable = false)
    PlanRouteCustomerBranch planRouteCustomerBranch;

    @Column(name = "product_id")
    Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "qty", precision = 25, scale = 2)
    BigDecimal qty = BigDecimal.ZERO;

    @Column(name = "price", precision = 25, scale = 2)
    BigDecimal price = BigDecimal.ZERO;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", updatable = false, insertable = false)
    Unit unit;

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CUSTOMER_BRANCH_PRODUCT;
    }

    @Override
    public String getEntityName() {
        return getProduct() != null ? getProduct().getName() : null;
    }
}
