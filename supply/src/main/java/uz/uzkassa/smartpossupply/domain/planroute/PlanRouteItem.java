package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:32
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_item", indexes = {
        @Index(columnList = "expeditor_id", name = "plan_route_item_plan_route_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_item SET deleted = true WHERE id=?")
public class PlanRouteItem extends BaseEntity implements Comparable<PlanRouteItem> {

    @Serial
    private static final long serialVersionUID = 242345343L;

    @Column(name = "plan_route_id")
    Long planRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_id", insertable = false, updatable = false)
    PlanRoute planRoute;

    @Column(name = "visit_plan_date")
    LocalDate visitPlanDate;

    @OneToMany(mappedBy = "planRouteItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    Set<PlanRouteCustomerBranch> planRouteCustomerBranches = new HashSet<>();

    public void addItem(PlanRouteCustomerBranch planRouteCustomerBranch) {
        planRouteCustomerBranch.setPlanRouteItem(this);
        planRouteCustomerBranch.setPlanRouteItemId(this.id);
        planRouteCustomerBranches.add(planRouteCustomerBranch);
    }

    @Override
    public int compareTo(PlanRouteItem o) {
        return this.visitPlanDate.compareTo(o.visitPlanDate);
    }
}
