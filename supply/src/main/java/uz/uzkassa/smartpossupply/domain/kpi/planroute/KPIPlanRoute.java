package uz.uzkassa.smartpossupply.domain.kpi.planroute;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.domain.kpi.client.KPIItem;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "kpi_plan_route")
@SQLDelete(sql = "UPDATE kpi SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPIPlanRoute extends KPI {

    @OneToMany(mappedBy = "kpiPlanRoute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<KPIPlanRouteBonus> bonuses = new ArrayList<>();

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    List<KPIItem> kpiItems = new ArrayList<>();

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.KPI_PLAN_ROUTE;
    }
}
