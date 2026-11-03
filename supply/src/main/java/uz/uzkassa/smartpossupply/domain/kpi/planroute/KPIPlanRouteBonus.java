package uz.uzkassa.smartpossupply.domain.kpi.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "kpi_plan_route_bonus")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRouteBonus extends BaseEntity {

    @Column(name = "percentage", precision = 25, scale = 2, nullable = false)
    BigDecimal percentage;

    @Column(name = "amount", precision = 25, scale = 2, nullable = false)
    BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_plan_route_id", nullable = false)
    KPIPlanRoute kpiPlanRoute;
}
