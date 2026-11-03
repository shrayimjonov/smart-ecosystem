package uz.uzkassa.smartpossupply.domain.kpi.client;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;

@Getter
@Setter
@Entity
@Table(name = "kpi_plan_route_item")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIItem extends BaseEntity {

    @Column(name = "plan_route_id")
    Long planRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_id", updatable = false, insertable = false)
    PlanRoute planRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    KPI kpi;
}
