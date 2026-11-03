package uz.uzkassa.smartpossupply.domain.planroutetemplate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "plan_route_template_expeditor", indexes = {
        @Index(columnList = "plan_route_template_id", name = "plan_route_template_expeditor_plan_route_template_id_idx"),
        @Index(columnList = "expeditor_id", name = "plan_route_template_expeditor_expeditor_id_idx"),
})
@Entity
@SQLDelete(sql = "UPDATE plan_route_template_expeditor SET deleted = true WHERE id=?")
public class PlanRouteTemplateExpeditor extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 822645L;

    @Column(name = "plan_route_template_id")
    Long planRouteTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_route_template_id", insertable = false, updatable = false)
    PlanRouteTemplate planRouteTemplate;

    @Column(name = "expeditor_id")
    Long expeditorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expeditor_id", insertable = false, updatable = false)
    User expeditor;
}
