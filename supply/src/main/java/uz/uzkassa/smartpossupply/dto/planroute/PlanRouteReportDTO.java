package uz.uzkassa.smartpossupply.dto.planroute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.company.CompanyBaseDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.10.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRouteReportDTO {

    CommonDTO agent;

    CompanyBaseDTO customer;

    BigDecimal totalPriceWithoutVat;

    BigDecimal totalVatPrice;

    BigDecimal totalPrice;

    CommonDTO branch;

    CommonDTO warehouse;

    CommonDTO category;

    BigDecimal totalProducts;

    PaymentType paymentType;

    public PlanRouteReportDTO(Long agentId, String agentFirstName, String agentLastName, Long customerId, String customerName, String customerTin, String customerBrand, BigDecimal totalPriceWithoutVat, BigDecimal totalVatPrice, BigDecimal totalPrice) {
        if (agentId != null) {
            this.agent = new CommonDTO(agentId, Optional.ofNullable(agentFirstName).orElse("").concat(" ").concat(Optional.ofNullable(agentLastName).orElse("")));
        }
        if (customerId != null) {
            this.customer = new CompanyBaseDTO(customerId, customerName, customerTin, customerBrand);
        }
        this.totalPriceWithoutVat = totalPriceWithoutVat;
        this.totalVatPrice = totalVatPrice;
        this.totalPrice = totalPrice;
    }

    public PlanRouteReportDTO(Long agentId, String agentFirstName, String agentLastName, Long customerId, String customerName,
                              String customerTin, String customerBrand, Long branchId, String branchName, Long warehouseId,
                              String warehouseName, Long categoryId, String categoryName, BigDecimal totalPriceWithoutVat,
                              BigDecimal totalVatPrice, BigDecimal totalPrice, BigDecimal totalProducts,  PaymentType paymentType) {
        if (agentId != null) {
            this.agent = new CommonDTO(agentId, Optional.ofNullable(agentFirstName).orElse("").concat(" ").concat(Optional.ofNullable(agentLastName).orElse("")));
        }
        if (customerId != null) {
            this.customer = new CompanyBaseDTO(customerId, customerName, customerTin, customerBrand);
        }
        this.totalPriceWithoutVat = totalPriceWithoutVat;
        this.totalVatPrice = totalVatPrice;
        this.totalPrice = totalPrice;
        if (warehouseId != null) {
            this.warehouse = new CommonDTO(warehouseId, warehouseName);
        }
        if (branchId != null) {
            this.branch = new CommonDTO(branchId, branchName);
        }
        if (categoryId != null) {
            this.category = new CommonDTO(categoryId, categoryName);
        }
        this.totalProducts = totalProducts;
        this.paymentType = paymentType;
    }
}
