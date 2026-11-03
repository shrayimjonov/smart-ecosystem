package uz.uzkassa.smartpossupply.domain.kpi.salesplan;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.Unit;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.category.Category;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "kpi_sales_plan_item")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KPISalesPlanItem extends BaseEntity {

    @Column(name = "product_id")
    Long productId;

    @ManyToOne
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    Product product;

    @Column(name = "sales_plan_value", nullable = false, precision = 25, scale = 2)
    BigDecimal salesPlanValue;

    @Column(name = "bonus_value", nullable = false, precision = 25, scale = 2)
    BigDecimal bonusValue;

    @Column(name = "category_id")
    Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    Category category;

    @Column(name = "unit_id")
    Long unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_sales_plan_id", nullable = false)
    KPISalesPlan kpiSalesPlan;

}
