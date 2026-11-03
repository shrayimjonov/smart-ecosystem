package uz.uzkassa.smartpossupply.domain.planroutetemplate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_template_customer_branch",indexes = {
        @Index(columnList = "customer_branch_id", name = "plan_route_template_customer_branch_customer_branch_id_idx"),
        @Index(columnList = "plan_route_template_item_id", name = "plan_route_template_customer_branch_plan_route_template_item_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_template_customer_branch SET deleted = true WHERE id=?")
public class PlanRouteTemplateCustomerBranch extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8512154L;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", insertable = false, updatable = false)
    CustomerBranch customerBranch;

    @NotNull
    @Column(name = "plan_route_template_item_id", nullable = false)
    Long planRouteTemplateItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_template_item_id", insertable = false, updatable = false)
    PlanRouteTemplateItem planRouteTemplateItem;
}
