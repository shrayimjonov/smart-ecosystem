package uz.uzkassa.smartpossupply.dto.customer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.11.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AccountReceivableMapDTO {

    Long operationId;
    String operationNumber;
    String operationType;
    Long stockOutId;
    String stockOutNumber;
    LocalDateTime operationDate;
    BigDecimal totalPrice;
    Long customerId;
    String customerName;
    String customerTin;
    String customerBrand;
    Long planRouteId;
    String planRouteNumber;
    Long agentId;
    String agentFirstName;
    String agentLastName;
    Long expeditorId;
    String expeditorFirstName;
    String expeditorLastName;
    Long facturaId;
    String facturaNo;
    String facturaStatus;
    LocalDate facturaDate;

    public AccountReceivableMapDTO(Long operationId, String operationNumber, String operationType, Long stockOutId, String stockOutNumber, LocalDateTime operationDate, BigDecimal totalPrice, Long customerId, String customerName, String customerTin, String customerBrand, Long planRouteId, String planRouteNumber, Long agentId, String agentFirstName, String agentLastName, Long expeditorId, String expeditorFirstName, String expeditorLastName, Long facturaId, String facturaNo, String facturaStatus, LocalDate facturaDate) {
        this.operationId = operationId;
        this.operationNumber = operationNumber;
        this.operationType = operationType;
        this.stockOutId = stockOutId;
        this.stockOutNumber = stockOutNumber;
        this.operationDate = operationDate;
        this.totalPrice = totalPrice;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerTin = customerTin;
        this.planRouteId = planRouteId;
        this.planRouteNumber = planRouteNumber;
        this.agentId = agentId;
        this.agentFirstName = agentFirstName;
        this.agentLastName = agentLastName;
        this.expeditorId = expeditorId;
        this.expeditorFirstName = expeditorFirstName;
        this.expeditorLastName = expeditorLastName;
        this.facturaId = facturaId;
        this.facturaNo = facturaNo;
        this.facturaStatus = facturaStatus;
        this.facturaDate = facturaDate;
        this.customerBrand = customerBrand;
    }
}
