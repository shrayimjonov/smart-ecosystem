package uz.uzkassa.smartpossupply.domain.planroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;

import java.io.Serial;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 19:32
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_expeditor",indexes = {
        @Index(columnList = "plan_route_id", name = "plan_route_expeditor_plan_route_id_idx"),
        @Index(columnList = "expeditor_id", name = "plan_route_expeditor_expeditor_id_idx"),
})
@Entity
public class PlanRouteExpeditor extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 2345368443L;

    @Column(name = "plan_route_id", nullable = false)
    Long planRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_id", insertable = false, updatable = false)
    PlanRoute planRoute;

    @Column(name = "expeditor_id", nullable = false)
    Long expeditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", insertable = false, updatable = false)
    User expeditor;
}
