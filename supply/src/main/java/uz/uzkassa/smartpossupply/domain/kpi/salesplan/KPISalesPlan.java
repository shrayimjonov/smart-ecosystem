package uz.uzkassa.smartpossupply.domain.kpi.salesplan;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.enums.BonusType;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.ProductAddType;
import uz.uzkassa.smartpossupply.enums.SalesPlanType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "kpi_sales_plan")
@SQLDelete(sql = "UPDATE kpi SET deleted = true WHERE id=?")
@SQLRestriction(value = "deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPISalesPlan extends KPI {

    @Enumerated(EnumType.STRING)
    @Column(name = "sales_plan_type", nullable = false)
    SalesPlanType salesPlanType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bonus_type", nullable = false)
    BonusType bonusType;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_add_type", nullable = false)
    ProductAddType productAddType;

    @OneToMany(mappedBy = "kpiSalesPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    List<KPISalesPlanItem> items = new ArrayList<>();

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.KPI_SALES_PLAN;
    }
}
