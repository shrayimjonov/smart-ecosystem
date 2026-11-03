package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.07.2023
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PlanRouteReportTotalQtyDTO {

    Long customerCount;

    Long customerBranchCount;

    Long agentCount;

    Long planRouteCount;

    BigDecimal totalPriceWithoutVat;

    BigDecimal totalVatPrice;

    BigDecimal totalPrice;

    BigDecimal totalProductCount;

    BigDecimal totalProducts;

    public PlanRouteReportTotalQtyDTO(Long customerCount, Long customerBranchCount, Long planRouteCount, Long agentCount, BigDecimal totalPriceWithoutVat, BigDecimal totalVatPrice, BigDecimal totalPrice, BigDecimal totalProductCount, BigDecimal totalProducts) {
        this.customerCount = customerCount;
        this.customerBranchCount = customerBranchCount;
        this.agentCount = agentCount;
        this.totalPriceWithoutVat = totalPriceWithoutVat;
        this.totalVatPrice = totalVatPrice;
        this.totalPrice = totalPrice;
        this.totalProductCount = totalProductCount;
        this.totalProducts = totalProducts;
        this.planRouteCount = planRouteCount;
    }
}
