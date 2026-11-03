package uz.uzkassa.smartpossupply.domain.planroutetemplate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.DayOfWeekEnum;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_template_item", indexes = {
        @Index(columnList = "plan_route_template_id", name = "plan_route_template_item_plan_route_template_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_template_item SET deleted = true WHERE id=?")
public class PlanRouteTemplateItem extends BaseEntity implements Comparable<PlanRouteTemplateItem> {

    @Serial
    private static final long serialVersionUID = 822645L;

    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    DayOfWeekEnum day;

    @Column(name = "plan_route_template_id")
    Long planRouteTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_template_id", insertable = false, updatable = false)
    PlanRouteTemplate planRouteTemplate;

    @OneToMany(mappedBy = "planRouteTemplateItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("deleted = false")
    @OrderBy("id")
    Set<PlanRouteTemplateCustomerBranch> customerBranches = new HashSet<>();

    public void addItem(PlanRouteTemplateCustomerBranch planRouteTemplateCustomerBranch) {
        planRouteTemplateCustomerBranch.setPlanRouteTemplateItem(this);
        planRouteTemplateCustomerBranch.setPlanRouteTemplateItemId(this.id);
        customerBranches.add(planRouteTemplateCustomerBranch);
    }

    @Override
    public int compareTo(PlanRouteTemplateItem o) {
        return Integer.compare(this.day.ordinal(), o.getDay().ordinal());
    }
}
