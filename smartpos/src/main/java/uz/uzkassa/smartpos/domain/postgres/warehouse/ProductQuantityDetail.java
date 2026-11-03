package uz.uzkassa.smartpos.domain.postgres.warehouse;

import java.math.BigDecimal;

public interface ProductQuantityDetail {
    BigDecimal getQty();

    Long getProductId();

    Long getUnitId();

    Integer getMarkingCount();

    BigDecimal getCostPrice();

    BigDecimal getSalesPrice();
}
