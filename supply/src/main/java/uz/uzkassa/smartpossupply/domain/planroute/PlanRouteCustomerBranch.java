package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.enums.PlanRouteCustomerBranchStatus;
import uz.uzkassa.smartpossupply.enums.PlanRouteReason;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:53
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_customer_branch",indexes = {
        @Index(columnList = "customer_branch_id", name = "plan_route_customer_branch_customer_branch_id_idx"),
        @Index(columnList = "plan_route_item_id", name = "plan_route_customer_branch_plan_route_item_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_customer_branch SET deleted = true WHERE id=?")
public class PlanRouteCustomerBranch extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2423L;

    @NotNull
    @Column(name = "customer_branch_id", nullable = false)
    Long customerBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", insertable = false, updatable = false)
    CustomerBranch customerBranch;

    @Column(name = "plan_route_item_id")
    Long planRouteItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_item_id", insertable = false, updatable = false)
    PlanRouteItem planRouteItem;

    @Column(name = "order_id")
    Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    PlanRouteCustomerBranchStatus status = PlanRouteCustomerBranchStatus.NOT_VISITED_YET;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    PlanRouteReason reason;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    String description;

    @Column(name = "unscheduled", columnDefinition = "boolean default false")
    boolean unscheduled;

    @OneToMany(mappedBy = "planRouteCustomerBranch", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    List<PlanRouteCustomerBranchAttachment> attachments = new ArrayList<>();

}
